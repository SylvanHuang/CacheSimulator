package cs233a7;
public class CacheBlock {
     boolean dirtyattr;
     long id;
     

    /** Block constructor, creates a block for the blocklist.
     * @param   inTag   The tag to add to the block.
     */
    public CacheBlock(long pTag) {
        this.id = pTag;
        this.dirtyattr = false;
    }

}
