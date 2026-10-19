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
import org.nd4j.nativeblas.OpaqueNDArray;

/**
 * Deallocator for OpaqueNDArray instances, integrated with the
 * DeallocatorService for deterministic native memory cleanup.
 */
@Slf4j
public class OpaqueNDArrayDeallocator implements Deallocatable, Deallocator {
    private final OpaqueNDArray array;
    private final long uniqueId;
    private final int targetDevice;
    private volatile boolean deallocated = false;
    private volatile boolean constant = false;

    public OpaqueNDArrayDeallocator(OpaqueNDArray array, long uniqueId, int targetDevice) {
        if (array == null) {
            throw new IllegalArgumentException("OpaqueNDArray cannot be null");
        }
        this.array = array;
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
            if (!constant && array != null && !array.isNull()) {
                org.nd4j.linalg.factory.Nd4j.getNativeOps().deleteNDArray(array);
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