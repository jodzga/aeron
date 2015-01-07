/*
 * Copyright 2014 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.aeron.common.concurrent.logbuffer;

import uk.co.real_logic.agrona.concurrent.UnsafeBuffer;

import static uk.co.real_logic.agrona.BitUtil.CACHE_LINE_SIZE;
import static uk.co.real_logic.agrona.BitUtil.SIZE_OF_INT;
import static uk.co.real_logic.aeron.common.concurrent.logbuffer.FrameDescriptor.FRAME_ALIGNMENT;

/**
 * Layout description log buffers buffers.
 */
public class LogBufferDescriptor
{
    /**
     * The log is currently clean or in use.
     */
    public static final int CLEAN = 0;

    /**
     * The log is dirty and requires cleaning.
     */
    public static final int NEEDS_CLEANING = 1;

    /**
     * Offset within the trailer where the tail value is stored.
     */
    public static final int TAIL_COUNTER_OFFSET;

    /**
     * Offset within the trailer where the high water mark is stored.
     */
    public static final int HIGH_WATER_MARK_OFFSET;

    /**
     * Offset within the trailer where current status is stored
     */
    public static final int STATUS_OFFSET;

    /**
     * Total length of the meta data buffer in bytes.
     */
    public static final int META_DATA_BUFFER_LENGTH;

    static
    {
        HIGH_WATER_MARK_OFFSET = 0;
        TAIL_COUNTER_OFFSET = HIGH_WATER_MARK_OFFSET + SIZE_OF_INT;
        STATUS_OFFSET = TAIL_COUNTER_OFFSET + CACHE_LINE_SIZE;
        META_DATA_BUFFER_LENGTH = CACHE_LINE_SIZE * 2;
    }

    /**
     * Minimum buffer size for a log term
     */
    public static final int MIN_TERM_SIZE = 64 * 1024; // TODO: make a sensible default

    /**
     * Check that term buffer is the correct size and alignment.
     *
     * @param buffer to be checked.
     * @throws IllegalStateException if the buffer is not as expected.
     */
    public static void checkTermBuffer(final UnsafeBuffer buffer)
    {
        final int capacity = buffer.capacity();
        if (capacity < MIN_TERM_SIZE)
        {
            final String s = String.format(
                "Term buffer capacity less than min size of %d, capacity=%d",
                MIN_TERM_SIZE,
                capacity);
            throw new IllegalStateException(s);
        }

        if ((capacity & (FRAME_ALIGNMENT - 1)) != 0)
        {
            final String s = String.format(
                "Term buffer capacity not a multiple of %d, capacity=%d",
                FRAME_ALIGNMENT,
                capacity);
            throw new IllegalStateException(s);
        }
    }

    /**
     * Check that meta data buffer is of sufficient size.
     *
     * @param buffer to be checked.
     * @throws IllegalStateException if the buffer is not as expected.
     */
    public static void checkMetaDataBuffer(final UnsafeBuffer buffer)
    {
        final int capacity = buffer.capacity();
        if (capacity < META_DATA_BUFFER_LENGTH)
        {
            final String s = String.format(
                "Meta data buffer capacity less than min size of %d, capacity=%d",
                META_DATA_BUFFER_LENGTH,
                capacity);
            throw new IllegalStateException(s);
        }
    }
}
