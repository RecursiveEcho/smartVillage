package com.backend.common.mq;

public final class MediaBindMqNames {

  private MediaBindMqNames() {}

  public static final String MEDIA_BIND_EXCHANGE = "media.bind.exchange";
  public static final String MEDIA_BIND_BUSINESS_QUEUE = "media.bind.business.queue";
  public static final String MEDIA_BIND_BUSINESS_ROUTING_KEY = "media.bind.business";
  public static final String MEDIA_BIND_AUTH_QUEUE = "media.bind.auth.queue";
  public static final String MEDIA_BIND_AUTH_ROUTING_KEY = "media.bind.auth";
  public static final String MEDIA_BIND_DLX = "media.bind.dlx";
  public static final String MEDIA_BIND_DLQ = "media.bind.dlq";
  public static final String MEDIA_BIND_DLQ_ROUTING_KEY = "media.bind.dlq";
}
