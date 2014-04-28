package org.fastcatsearch.analytics.http;

import org.fastcatsearch.analytics.http.action.ActionResponse;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public interface HttpChannel {
	void sendResponse(ActionResponse response);
	void sendError(HttpResponseStatus status, Throwable e);
	void sendHeader(ActionResponse response);
	Channel channel();
	void close();
}
