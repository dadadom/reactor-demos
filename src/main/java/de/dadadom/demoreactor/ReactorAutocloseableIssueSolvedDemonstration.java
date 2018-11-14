package de.dadadom.demoreactor;

import reactor.core.publisher.Mono;

/**
 * A possible solution to the issue presented in {@link ReactorAutocloseableIssueDemonstration}.
 */
public class ReactorAutocloseableIssueSolvedDemonstration
{

    public static void main(String[] args)
    {
        FakeDatabaseConnection dbConnection = new FakeDatabaseConnection();
        Mono<String> databaseFetchingStream = Mono.just(dbConnection)
                .doOnNext(i -> System.out.println("before the map to 'readData'"))
                .map(FakeDatabaseConnection::readData)
                .doOnNext(i -> System.out.println("after the map to 'readData'"))
                .doFinally(ignored -> dbConnection.close());

        System.out.println("Mono created");

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
