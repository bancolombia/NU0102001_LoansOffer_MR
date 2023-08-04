package co.com.bancolombia.libreinversion.amazon;


import co.com.bancolombia.libreinversion.model.request.gateways.AmazonS3Gateways;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.Optional;


@AllArgsConstructor
@Component
public class AmazonS3Adapter implements AmazonS3Gateways {


    private final S3AsyncClient s3AsyncClient;
    private final S3Presigner s3Presigner = S3Presigner.create();
    private static TechLogger log = LoggerFactory.getLog(AmazonS3Adapter.class.getName());

    @Override
    public Mono<Boolean> putObject(String fileName, byte[] fileData, String bucketName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(fileName).build();
        return Mono.fromFuture(s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromBytes(fileData)))
                .flatMap(response -> Mono.just(true));
    }

    @Override
    public InputStream getObjectAsInputStream(String fileName, String bucketName) {
        GetObjectRequest getObjectRequest = GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return s3AsyncClient
                .getObject(getObjectRequest, AsyncResponseTransformer.toBytes()).join().asInputStream();
    }

    @Override
    public String generatePresignedURL(String bucket,
                                       String file,
                                       Long minutes) {

        PresignedGetObjectRequest presignedGetObjectRequest =
                s3Presigner.presignGetObject(objectPresignRequest(bucket, file, minutes));

        try {
            HttpURLConnection connection = (HttpURLConnection) presignedGetObjectRequest.url().openConnection();
            presignedGetObjectRequest.httpRequest().headers().forEach((header, values) ->
                    values.forEach(value -> connection.addRequestProperty(header, value))
            );

            if (presignedGetObjectRequest.signedPayload().isPresent()) {
                connection.setDoOutput(true);
                Optional<SdkBytes> value = presignedGetObjectRequest.signedPayload();
                if (value.isPresent()) {
                    InputStream signedPayload = value.get().asInputStream();
                    OutputStream httpOutputStream = connection.getOutputStream();
                    IoUtils.copy(signedPayload, httpOutputStream);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return presignedGetObjectRequest.url().toString();
    }

    private GetObjectPresignRequest objectPresignRequest(String bucket, String file, Long minutes) {
        return GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(minutes))
                .getObjectRequest(objetRequest(bucket, file))
                .build();
    }

    private GetObjectRequest objetRequest(String bucket, String file) {
        return GetObjectRequest.builder()
                .bucket(bucket)
                .key(file)
                .build();
    }

    @Override
    public Mono<byte[]> getObjectAsByteArray(String fileName, String bucketName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return Mono.just(s3AsyncClient
                .getObject(getObjectRequest, AsyncResponseTransformer.toBytes()).join().asByteArray());
    }
}
