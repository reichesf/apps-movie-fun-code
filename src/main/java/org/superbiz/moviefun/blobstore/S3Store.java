package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;


public class S3Store implements BlobStore
{
    AmazonS3Client amazonS3Client;
    String s3BucketName;

    public S3Store(AmazonS3Client s3Client, String s3BucketName)
    {
        this.amazonS3Client = s3Client;
        this.s3BucketName = s3BucketName;
    }


    @Override
    public void put(Blob blob) throws IOException
    {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(blob.getContentType());
        this.amazonS3Client.putObject(this.s3BucketName, blob.getName(), blob.getInputStream(), objectMetadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException
    {

        Blob blob=null;

        S3Object s3Object = this.amazonS3Client.getObject(this.s3BucketName, name);

        if (s3Object != null)
        {
            blob = new Blob(name, s3Object.getObjectContent(), s3Object.getObjectMetadata().getContentType());

            Optional.of(blob);
        }

        return Optional.of(blob);
    }

    @Override
    public void deleteAll()
    {

    }
}
