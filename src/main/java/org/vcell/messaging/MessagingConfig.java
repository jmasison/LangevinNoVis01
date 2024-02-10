package org.vcell.messaging;

public record MessagingConfig(
        String broker_host,
        int broker_port,
        String broker_username,
        String broker_password,
        String compute_hostname,
        String vc_username,
        String simKey,
        int taskID,
        int jobIndex
) {}