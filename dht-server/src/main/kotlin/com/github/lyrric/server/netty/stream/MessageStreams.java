package com.github.lyrric.server.netty.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MessageStreams {

	@Output(value = "download-message-out")
	MessageChannel downloadMessageOutput();

}
