package org.tron.core.db;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.ByteUtil;
import org.tron.common.utils.Sha256Hash;

@Slf4j
@Component
public class CommonDataBase extends TronDatabase<byte[]> {

  private static final byte[] LATEST_PBFT_BLOCK_NUM = "LATEST_PBFT_BLOCK_NUM".getBytes();
  private static final byte[] LATEST_PBFT_BLOCK_HASH = "LATEST_PBFT_BLOCK_HASH".getBytes();

  public CommonDataBase() {
    super("common-database");
  }

  @Override
  public void put(byte[] key, byte[] item) {
    dbSource.putData(key, item);
  }

  @Override
  public void delete(byte[] key) {
    dbSource.deleteData(key);
  }

  @Override
  public byte[] get(byte[] key) {
    return dbSource.getData(key);
  }

  @Override
  public boolean has(byte[] key) {
    return dbSource.getData(key) != null;
  }

  public void saveLatestPbftBlockNum(long number) {
    if (number <= getLatestPbftBlockNum()) {
      logger.warn("pbft number {} <= latest number {}", number, getLatestPbftBlockNum());
      return;
    }
    this.put(LATEST_PBFT_BLOCK_NUM, ByteArray.fromLong(number));
  }

  public long getLatestPbftBlockNum() {
    return Optional.ofNullable(get(LATEST_PBFT_BLOCK_NUM))
        .map(ByteArray::toLong)
        .orElse(0L);
  }

  public void saveLatestPbftBlockHash(byte[] data) {
    this.put(LATEST_PBFT_BLOCK_HASH, data);
  }

  public Sha256Hash getLatestPbftBlockHash() {
    byte[] date = this.get(LATEST_PBFT_BLOCK_HASH);

    if (ByteUtil.isNullOrZeroArray(date)) {
      return null;
    }
    return Sha256Hash.wrap(date);
  }

}
