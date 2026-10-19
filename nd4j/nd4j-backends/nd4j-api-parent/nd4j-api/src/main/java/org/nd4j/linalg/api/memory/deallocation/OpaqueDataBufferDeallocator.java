/*
 *  ******************************************************************************
 *  *
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Apache License, Version 2.0 which is available at
 *  * https://www.apache.org/licenses/LICENSE-2.0.
 *  ******************************************************************************
 */

package org.nd4j.linalg.api.memory.deallocation;

import lombok.extern.slf4j.Slf4j;
import org.nd4j.linalg.api.memory.Deallocatable;
import org.nd4j.linalg.api.memory.Deallocator;
import org.nd4j.nativeblas.OpaqueDataBuffer;

/**
 * Deallocator for OpaqueDataBuffer instances, integrated with the
 * DeallocatorService for deterministic native memory cleanup.
 */
@Slf4j
public class OpaqueDataBufferDeallocator implements Deallocatable, Deallocator {
    private final OpaqueDataBuffer buffer;
    private final long uniqueId;
    private final int targetDevice;
    private volatile boolean deallocated = false;
    private volatile boolean constant = false;

    public OpaqueDataBufferDeallocator(OpaqueDataBuffer buffer, long uniqueId, int targetDevice) {
        if (buffer == null) {
            throw new IllegalArgumentException("OpaqueDataBuffer cannot be null");
        }
        this.buffer = buffer;
        this.uniqueId = uniqueId;
        this.targetDevice = targetDevice;
    }

    @Override
    public void deallocate() {
        if (deallocated) {
            return;
        }
        synchronized (this) {
            if (deallocated) {
                return;
            }
            deallocated = true;
            if (!constant && buffer != null && !buffer.isNull()) {
                org.nd4j.linalg.factory.Nd4j.getNativeOps().deleteDataBuffer(buffer);
            }
        }
    }

    /** Returns true if the native memory has already been released. */
    public boolean isDeallocated() {
        return deallocated;
    }

    @Override
    public boolean isConstant() {
        return constant;
    }

    @Override
    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    @Override
    public boolean shouldDeAllocate() {
        return !constant;
    }

    @Override
    public long getUniqueId() {
        return uniqueId;
    }

    @Override
    public Deallocator deallocator() {
        return this;
    }

    @Override
    public int targetDevice() {
        return targetDevice;
    }
}