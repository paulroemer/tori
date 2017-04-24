package org.vaadin.tori.util;

public interface ToriMailService {
    void sendUserAuthored(long postId, String formattedPostBody);
}
