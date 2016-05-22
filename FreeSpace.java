public class FreeSpace implements Comparable<FreeSpace> {
    private int address;
    private int size;
    
    public FreeSpace(int address, int size) {
        this.address = address;
        this.size = size;
    }
    
    public int getAddress() {
        return address;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setAddress(int address) {
        this.address = address;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    @Override
    public int compareTo(FreeSpace fs) {
        return (this.getSize() < fs.getSize()? -1 :
        this.getSize() == fs.getSize()? 0 : 1);
    }
}