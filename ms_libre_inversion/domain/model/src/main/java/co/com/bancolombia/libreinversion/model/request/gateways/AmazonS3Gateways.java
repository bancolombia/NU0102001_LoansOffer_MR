package co.com.bancolombia.libreinversion.model.request.gateways;

import reactor.core.publisher.Mono;

import java.io.InputStream;

public interface AmazonS3Gateways {
    Mono<Boolean> putObject(String fileName, byte[] fileData, String bucketName);
    InputStream getObjectAsInputStream(String fileName, String bucketName);
    String  generatePresignedURL(String bucketName, String objectKey, Long minutes);
    Mono<byte[]> getObjectAsByteArray(String fileName, String bucketName);
}
