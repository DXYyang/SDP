package org.apache.lucene.store;

import java.io.IOException;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A memory-resident {@link IndexInput} implementation.
 * 
 * @version $Id$
 */

class RAMInputStream extends IndexInput implements Cloneable {
  static final int BUFFER_SIZE = RAMOutputStream.BUFFER_SIZE;

  private RAMFile file;
  private long length;

  private byte[] currentBuffer;
  private int currentBufferIndex;
  
  private int bufferPosition;
  private long bufferStart;
  private int bufferLength;

  public RAMInputStream(RAMFile f) {
    file = f;
    length = file.length;

    // make sure that we switch to the
    // first needed buffer lazily
    currentBufferIndex = -1;
    currentBuffer = null;
  }

  public void close() {
    // nothing to do here
  }

  public long length() {
    return length;
  }

  public byte readByte() throws IOException {
    if (bufferPosition >= bufferLength) {
      currentBufferIndex++;
      switchCurrentBuffer();
    }
    return currentBuffer[bufferPosition++];
  }

  public void readBytes(byte[] b, int offset, int len) throws IOException {
    while (len > 0) {
      if (bufferPosition >= bufferLength) {
        currentBufferIndex++;
        switchCurrentBuffer();
      }

      int remainInBuffer = bufferLength - bufferPosition;
      int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;
      System.arraycopy(currentBuffer, bufferPosition, b, offset, bytesToCopy);
      offset += bytesToCopy;
      len -= bytesToCopy;
      bufferPosition += bytesToCopy;
    }
  }

  private final void switchCurrentBuffer() throws IOException {
    if (currentBufferIndex >= file.buffers.size()) {
      // end of file reached, no more buffers left
      throw new IOException("Read past EOF");
    } else {
      currentBuffer = (byte[]) file.buffers.get(currentBufferIndex);
      bufferPosition = 0;
      bufferStart = BUFFER_SIZE * currentBufferIndex;
      bufferLength = (int) (length - bufferStart);
      if (bufferLength > BUFFER_SIZE) {
        bufferLength = BUFFER_SIZE;
      }
    }
  }

  public long getFilePointer() {
    return currentBufferIndex < 0 ? 0 : bufferStart + bufferPosition;
  }

  public void seek(long pos) throws IOException {
    long bufferStart = currentBufferIndex * BUFFER_SIZE;
    if (pos < bufferStart || pos >= bufferStart + BUFFER_SIZE) {
      currentBufferIndex = (int) (pos / BUFFER_SIZE);
      switchCurrentBuffer();
    }
    bufferPosition = (int) (pos % BUFFER_SIZE);
  }
}
