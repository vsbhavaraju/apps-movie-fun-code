package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class S3Store implements BlobStore {
    private AmazonS3Client amazonS3Client;
    private String s3BucketName;

    public S3Store(AmazonS3Client s3Client, String s3BucketName) {
        this.amazonS3Client = s3Client;
        this.s3BucketName = s3BucketName;

    }

    @Override
    public void put(Blob blob) throws IOException {

        List<Bucket> buckets = amazonS3Client.listBuckets();
        if (buckets.isEmpty())
            amazonS3Client.createBucket(s3BucketName);

        ObjectMetadata omd  = new ObjectMetadata();
        omd.setContentType(blob.getContentType());
        PutObjectRequest req = new PutObjectRequest(s3BucketName, blob.getName(), blob.getInputStream(), omd);
        amazonS3Client.putObject(req);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        S3Object s3o = amazonS3Client.getObject(s3BucketName, name);

        return Optional.of(new Blob(s3o.getKey(), s3o.getObjectContent(), s3o.getObjectMetadata().getContentType()));
    }

    @Override
    public void deleteAll() {
        ObjectListing ol = amazonS3Client.listObjects(s3BucketName);
        List<S3ObjectSummary> summaries = ol.getObjectSummaries();
        summaries.stream().forEach(s -> amazonS3Client.deleteObject(s3BucketName, s.getKey()));
    }
}
