package pl.fnfcinema.cinema

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.mockserver.client.MockServerClient
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.utility.DockerImageName
import java.net.URI

class MockServerExtension : BeforeAllCallback, BeforeEachCallback, AfterAllCallback {

    override fun beforeAll(context: ExtensionContext?) {
        mockServer.start()
    }

    override fun beforeEach(context: ExtensionContext?) {
        mockServerClient().reset()
    }

    override fun afterAll(context: ExtensionContext?) {
        mockServer.stop()
    }

    class MockServer {

        lateinit var mockServerClient: MockServerClient

        fun start() {
            mockServer.start()
            mockServerClient = MockServerClient(mockServer.host, mockServer.serverPort)
        }

        fun stop() {
            mockServer.stop()
        }

        fun endpoint(): String = mockServer.endpoint

        companion object {
            private val mockServer: MockServerContainer = MockServerContainer(
                DockerImageName.parse("mockserver/mockserver")
                    .withTag("mockserver-" + MockServerClient::class.java.getPackage().implementationVersion)
            )
        }
    }

    companion object {

        val mockServer: MockServer = MockServer()

        fun mockServerClient() = mockServer.mockServerClient
        fun serverUri(): URI = URI.create(mockServer.endpoint())
    }
}