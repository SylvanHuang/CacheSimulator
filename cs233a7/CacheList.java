
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A blocklist class.
 */
public class CacheList {
    private int size;
    private long index;
    public int cycles;
    private Deque<CacheBlock> cacheList;

    /**
     * Creates a Blocklist with Deque data structure.
     * @param   index   Current index of the BlockList
     * @param   evic    Eviction = 1 ? LRU : FIFO
     * @param   size    Size of the blocklist
     */
    public CacheList(long inIndex, int inSize) {
        this.size = inSize;
        this.cycles = 0;
        this.cacheList = new ArrayDeque<>();
        this.index = inIndex;
        
    }

    /**
     * Adds a block to the Blocklist.
     * @param   b   Block to add to the blocklist
     */
    public void addBlock(CacheBlock b) {
        if (this.cacheList.size() == this.size) {
            this.removeLast();
            this.cacheList.addFirst(b);
        } else {
            this.cacheList.addFirst(b);
        }

    }

    /**
     * Removes the last item from the deque.
     */
    public void removeLast() {
        if (this.cacheList.getLast().dirtyattr) {
            this.cycles += CacheSimulator.CYCLES * (CacheSimulator.cacheBytesBlock / CacheSimulator.WORD);
        }
        this.cacheList.removeLast();
    }

    /**
     * Returns eviction cycles.
     * @return  the number of eviction cycles from the cacheList
     */
    /*public int getEvicCycles() {
        return this.cycles;
    }*/

    /**
     * Returns whether or not the cacheList contains the block.
     * @return  true if the blocklist contains b, false otherwise
     */
    public boolean contains(CacheBlock b) {
        if (this.cacheList.contains(b)) {

            boolean isdirty = false;
            for (CacheBlock temp: this.cacheList) {
                if (temp.equals(b)) {
                    isdirty = temp.dirtyattr;
                }
            }
            this.cacheList.remove(b);
            this.cacheList.addFirst(b);
            if (isdirty) {
                this.setDirty(b);
            }
            return true;
        }
        return this.cacheList.contains(b);
    }

    /**
     * Sets a block to dirty.
     * @param   other   Block to set dirty
     */
    public void setDirty(CacheBlock other) {
        for (CacheBlock block: this.cacheList) {
            if (block.equals(other)) {
                block.dirtyattr = true;
            }
        }
    }

    @Override
    /**
     * Returns if the Blocklist is equal to parameter, on index.
     * @param   obj The object to compare to the Blocklist
     * @return  True if they are the same object, false otherwise
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CacheList other = (CacheList) obj;
        if (this.index != other.index) {
            return false;
        }
        return true;
    }

}
