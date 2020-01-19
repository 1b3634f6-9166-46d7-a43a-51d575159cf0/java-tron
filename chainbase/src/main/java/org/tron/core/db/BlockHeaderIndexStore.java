package org.tron.core.db;

import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.capsule.BlockCapsule.BlockId;
import org.tron.core.capsule.BytesCapsule;
import org.tron.core.exception.ItemNotFoundException;

@Component
public class BlockHeaderIndexStore extends TronStoreWithRevoking<BytesCapsule> {

  private static final String SPLIT = "_";

  @Autowired
  public BlockHeaderIndexStore(@Value("block-header-index") String dbName) {
    super(dbName);

  }

  public void put(String chainId, BlockId id) {
    put(buildKey(chainId, id.getNum()), new BytesCapsule(id.getBytes()));
  }

  public BlockId get(String chainId, Long num)
      throws ItemNotFoundException {
    BytesCapsule value = getUnchecked(buildKey(chainId, num));
    if (value == null || value.getData() == null) {
      throw new ItemNotFoundException("number: " + num + " is not found!");
    }
    return new BlockId(Sha256Hash.wrap(value.getData()), num);
  }

  public BlockId getUnchecked(String chainId, Long num) {
    BytesCapsule value = getUnchecked(buildKey(chainId, num));
    if (value == null || value.getData() == null) {
      return null;
    }
    return new BlockId(Sha256Hash.wrap(value.getData()), num);
  }

  @Override
  public BytesCapsule get(byte[] key)
      throws ItemNotFoundException {
    byte[] value = revokingDB.getUnchecked(key);
    if (ArrayUtils.isEmpty(value)) {
      throw new ItemNotFoundException("number: " + Arrays.toString(key) + " is not found!");
    }
    return new BytesCapsule(value);
  }

  private byte[] buildKey(String chainId, Long num) {
    return (chainId + SPLIT + num).getBytes();
  }
}