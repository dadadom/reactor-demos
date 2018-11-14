package de.dadadom.demoreactor;

import reactor.core.publisher.Mono;

/**
 * Demonstrate the issue with reactive streams and {@link AutoCloseable}. If the closeable class is e.g. a database connection, the connection might be closed before any action has been done on that connection.
 */
public class ReactorAutocloseableIssueDemonstration
{

    public static void main(String[] args)
    {
        Mono<String> databaseFetchingStream;
        try (FakeDatabaseConnection x = new FakeDatabaseConnection()) {
            databaseFetchingStream = Mono.just(x)
                    .doOnNext(i -> System.out.println("before the map to 'readData'"))
                    .map(FakeDatabaseConnection::readData)
                    .doOnNext(i -> System.out.println("after the map to 'readData'"));

            System.out.println("Mono created");
        }

        databaseFetchingStream.subscribe(data -> System.out.println("State of the data: " + data));
    }

    private static final class FakeDatabaseConnection implements AutoCloseable
    {

        private boolean closed = false;

        public String readData()
        {
            if (closed) {
                throw new IllegalStateException("Oops, the connection is closed, don't do anything here!");
            }
            return "Congratulations, you just read some data.";
        }

        @Override
        public void close()
        {
            closed = true;
            System.out.println("Closing fake database connection");
        }
    }
}
