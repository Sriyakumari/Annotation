package Controller;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;

public class GlobalTracingConfig {
    public static void initializeGlobalTracer() {
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder().build();
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(spanExporter))
                .build();

        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();
        // Set the global OpenTelemetry instance
        io.opentelemetry.api.GlobalOpenTelemetry.set(openTelemetrySdk);
    }
}
