package org.knulikelion.moneyisinvest.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NCloudStorageConfiguation {
    private final String accessKey;

    private final String secretKey;

    private final String region;

    private final String endPoint;

    public NCloudStorageConfiguation(@Value("${cloud.aws.credentials.access-key}") String accessKey,
                                      @Value("${cloud.aws.credentials.secret-key}") String secretKey,
                                      @Value("${cloud.aws.region.static}") String region,
                                      @Value("${cloud.aws.s3.endpoint}") String endPoint) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.endPoint = endPoint;
    }

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey,secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, region))
                .build();
    }
}
