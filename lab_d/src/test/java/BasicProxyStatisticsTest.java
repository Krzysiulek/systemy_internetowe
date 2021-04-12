import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import proxy.Response;
import statistics.BasicProxyStatistics;
import statistics.Statistics;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

public class BasicProxyStatisticsTest {
    private final static int THREADS_NUM = 50;
    private final static int REPETITIONS_PER_THREAD = 500;
    private BasicProxyStatistics basicProxyStatistics;

    @Before
    public void cleanUp() throws
                          IOException {
        basicProxyStatistics = new BasicProxyStatistics();
    }


    @Test
    public void testSent() {

        //given
        ArrayList<Thread> threads = new ArrayList<>();

        // when
        for (int i = 0; i < THREADS_NUM; i++) {
            Thread thread = new Thread(this::addResponseSent);
            threads.add(thread);
            thread.start();
        }

        threads.forEach(this::joinThread);

        // then
        Statistics statistics = basicProxyStatistics.getStatisticsSentMap()
                                                    .get("www.google.com");

        Assert.assertEquals(THREADS_NUM * REPETITIONS_PER_THREAD, statistics.getRequestCount());
    }

    @Test
    public void testReceived() {

        //given
        ArrayList<Thread> threads = new ArrayList<>();

        // when
        for (int i = 0; i < THREADS_NUM; i++) {
            Thread thread = new Thread(this::addResponseReceived);
            threads.add(thread);
            thread.start();
        }

        threads.forEach(this::joinThread);

        // then
        Statistics statistics = basicProxyStatistics.getStatisticsReceivedMap()
                                                    .get("www.google.com");

        Assert.assertEquals(THREADS_NUM * REPETITIONS_PER_THREAD, statistics.getRequestCount());
    }

    @SneakyThrows
    private void addResponseSent() {
        Response response = new Response("dasd".getBytes(), 4, 404, new URI("https://www.google.com"), null);

        for (int i = 0; i < REPETITIONS_PER_THREAD; i++) {
            basicProxyStatistics.addSentData(response);
        }
    }

    @SneakyThrows
    private void addResponseReceived() {
        Response response = new Response("dasd".getBytes(), 4, 404, new URI("https://www.google.com"), null);

        for (int i = 0; i < REPETITIONS_PER_THREAD; i++) {
            basicProxyStatistics.addReceivedData(response);
        }
    }

    @SneakyThrows
    private void joinThread(Thread thread) {
        thread.join();
    }

}
