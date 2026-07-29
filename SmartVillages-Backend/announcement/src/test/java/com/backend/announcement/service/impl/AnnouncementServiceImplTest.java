package com.backend.announcement.service.impl;

import com.backend.announcement.mapper.AnnouncementMapper;
import com.backend.common.support.AuthUserQueryService;
import com.backend.common.utils.RedisDistributedLock;
import com.backend.common.utils.RedisJsonCacheTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import static org.mockito.Mockito.verify;
import org.springframework.transaction.support.TransactionSynchronization;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceImplTest {

  @Mock
  private RedisDistributedLock redisDistributedLock;

  @Mock
  private AnnouncementMapper announcementMapper;

  @Mock
  private RedisJsonCacheTool redisJsonCacheTool;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private AuthUserQueryService authUserQueryService;

  @InjectMocks
  private AnnouncementServiceImpl announcementService;

  // 清除事务同步
  @AfterEach
  void clearTransactionSynchronization() {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.clearSynchronization();
    }
  }

  @Test
  // 测试事务提交后运行
  void invalidateCachesAfterCommit_doesNotRunBeforeCommit() {

    // 初始化事务同步管理器
    TransactionSynchronizationManager.initSynchronization();

    // 调用私有方法
    ReflectionTestUtils.invokeMethod(announcementService, "invalidateCachesAfterCommit", 1L);

    // 回调只是完成注册，提交前不能操作缓存
    verifyNoInteractions(redisJsonCacheTool);
  }

  @Test
  // 测试事务提交后运行
  void invalidateCachesAfterCommit_runsBothCacheOperationsAfterCommit() {

    TransactionSynchronizationManager.initSynchronization();

    ReflectionTestUtils.invokeMethod(announcementService, "invalidateCachesAfterCommit", 1L);

    // 模拟事务提交
    for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
      synchronization.afterCommit();
    }

    // 验证在事务提交后调用了缓存操作
    verify(redisJsonCacheTool).delete("announcement:detail:1");
    // 验证在事务提交后调用了缓存操作
    verify(redisJsonCacheTool).bumpListCacheVersion("announcement:list:published:ver");

  }

  @Test
  // 测试事务回滚后不运行缓存操作
  void invalidateCachesAfterCommit_doesNotRunCacheOperationsAfterRollback() {
    TransactionSynchronizationManager.initSynchronization();

    ReflectionTestUtils.invokeMethod(announcementService, "invalidateCachesAfterCommit", 1L);

    // 模拟事务回滚
    for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
      synchronization.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
    }

    // 验证在事务回滚后没有调用缓存操作
    verifyNoInteractions(redisJsonCacheTool);
  }
}
