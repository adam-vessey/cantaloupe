package edu.illinois.library.cantaloupe.processor;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import edu.illinois.library.cantaloupe.config.Configuration;
import edu.illinois.library.cantaloupe.config.Key;
import edu.illinois.library.cantaloupe.image.Format;

import static edu.illinois.library.cantaloupe.test.PerformanceTestConstants.*;

/**
 * Executes benchmark to compare the speed of reading TIFF files.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = WARMUP_ITERATIONS,
        time = WARMUP_TIME,
        timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = MEASUREMENT_ITERATIONS,
        time = MEASUREMENT_TIME,
        timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = { "-server", "-Xms128M", "-Xmx128M", "-Dcantaloupe.config=memory"} )
public class OpenJpegProcessorPerformance extends OpenJpegProcessorTest {
    protected Format intermediateFormat;
    public void setUp() throws Exception {
        super.setUp();
        final Configuration config = Configuration.getInstance();
        if (System.getProperty(Key.TEMP_PATHNAME.key()) != null) {
            config.setProperty(Key.TEMP_PATHNAME, System.getProperty(Key.TEMP_PATHNAME.key()));
        }
    }

    @Benchmark
    @Override
    public void testReadImageInfoOnAllFixtures() throws Exception {
        super.testReadImageInfoOnAllFixtures();
    }
    
    
    @Benchmark
    public void testReadImageInfoOnAllFixturesBMP() throws Exception {
        setIntermediateFormat(Format.BMP);
        testReadImageInfoOnAllFixtures();
    }
    
    @Benchmark
    public void testReadImageInfoOnAllFixturesTIFF() throws Exception {
        setIntermediateFormat(Format.TIF);
        testReadImageInfoOnAllFixtures();
    }
    
    @Benchmark
    @Override
    public void testProcessOnAllFixtures() throws Exception {
        super.testProcessOnAllFixtures();
    }
    
    
    @Benchmark
    public void testProcessOnAllFixturesBMP() throws Exception {
        setIntermediateFormat(Format.BMP);
        testProcessOnAllFixtures();
    }
    
    @Benchmark
    public void testProcessOnAllFixturesTIFF() throws Exception {
        setIntermediateFormat(Format.TIF);
        testProcessOnAllFixtures();
    }
    
    protected void setIntermediateFormat(Format format) {
        intermediateFormat = format;
    }
    
    @Override
    protected OpenJpegProcessor newInstance() {
        OpenJpegProcessor proc = super.newInstance();
        if (intermediateFormat != null) {
            proc.setIntermediateFormat(intermediateFormat);
        }
        return proc;
    }
    
}
