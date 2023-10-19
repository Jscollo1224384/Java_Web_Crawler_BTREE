import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class BTree2{
    int minDegree;
    int maxKeys;
    byte[] inputBytes;
    int numberOfBools;
    ByteBuffer buffer;
    int NODESIZE;
    int NumberOfChildPointers;
    int NumberOfKeys;
    int bufferCapacity;
    int nodeIdentifier;
    Node root;
    File file;
    RandomAccessFile raf;
    FileChannel fc;
    long nodeNum;
    int blockSize;

    public BTree2(int t,String n) throws IOException, ClassNotFoundException {
        file = new File(n + ".txt");
        //file.createNewFile();
        raf = new RandomAccessFile(file, "rw");
        minDegree = t;
        maxKeys = 2 * minDegree - 1;
        nodeNum = 0;
        NumberOfChildPointers = maxKeys + 1;
        nodeIdentifier = 8;
        numberOfBools = 1;
        NumberOfKeys = maxKeys;
        NODESIZE = ((NumberOfChildPointers * 8)  + (NumberOfKeys * 4) + (NumberOfKeys * 4)+ (numberOfBools * 4) + nodeIdentifier);
        bufferCapacity = NODESIZE;
        if (raf.length()==0) {

            root = new Node(0, true);
            root.nId = 0;
            root.diskWrite(root);
        }
        else{

            Node r;
            if(raf.length() > 77)
                r = new Node(0,false);
            else
                r = new Node(0,true);

            r = r.diskRead(0);

            root = r;
        }
    }

    public void insert(int key) throws IOException, ClassNotFoundException {

        int k = key;
        Node r;
        r = root;
        boolean collision = false;
        if(r.n == maxKeys){
            for (int i = 0; i < r.keys.length ; i++) {
                if(r.keys[i][0] == k) {
                    r.keys[i][1]++;
                    r.diskWrite(r);

                    collision = true;
                }
            }
            if(!collision) {
                Node s = new Node(0, false);
                s.nId = 0;
                r.nId = allocateNode(r);
                s.childPointer[0] = r.nId;
                s.splitChild(r, 0, s);
                Node temp;
                temp = r.diskRead(0);
                temp.insertNonFull(temp, k);
                root = temp;
            }
        }
        else{
            r.insertNonFull(r,k);
        }
    }
    public int search(int key)throws IOException{
        Node x;
        if(!file.exists()) {
            x = root;
            return x.bTSearch(x, key);
        }
        else
            x = root;
            x = x.diskRead(0);
        return x.bTSearch(x,key);
    }
    public long allocateNode(Node x)throws IOException{

        raf = new RandomAccessFile(file,"rw");
        raf.seek(raf.length());
        nodeNum = raf.getFilePointer();
        x.nId = nodeNum;
        x.diskWrite(x);
        return x.nId;
    }

    public class Node {
        int n;
        public int[][] keys;
        public long[] childPointer;
        public boolean leaf;
        long nId;
        public Node(int n, boolean leaf) {
            this.n = n;
            this.leaf = leaf;
            keys = new int[maxKeys][2];
            nId = nodeNum;
            if (leaf)
                childPointer = null;
            else
                childPointer = new long[maxKeys + 1];
        }
        public void diskWrite(Node x) throws IOException {

            raf = new RandomAccessFile(file,"rw");
            fc =  raf.getChannel();
            long startPos = x.nId;
            raf.seek(startPos);
            buffer = ByteBuffer.allocate(bufferCapacity);
            int key;
            int val;

            if(x.leaf){

                buffer.putLong(x.nId);
                int bool = 1;
                buffer.putInt(bool);
                for (int i = 0; i < x.keys.length; i++){
                    if(x.keys[i][0]== 0){
                        key = 0;
                        val = 0;
                        buffer.putInt(key);
                        buffer.putInt(val);
                    }
                    else {
                        key = x.keys[i][0];
                        val = x.keys[i][1];
                        buffer.putInt(key);
                        buffer.putInt(val);
                    }
                }
                for (int i = -1; i < x.keys.length ; i++) {
                    buffer.putLong(-1);
                }
                buffer.flip();
                fc.write(buffer);
                buffer.clear();
                fc.close();
                raf.close();
            }
            else {
                buffer.putLong(x.nId);
                int bool = 0;
                buffer.putInt(bool);

                for (int i = 0; i < x.keys.length; i++) {
                    if(x.keys[i][0]== 0){
                        key = 0;
                        val = 0;
                        buffer.putInt(key);
                        buffer.putInt(val);
                    }
                    else{
                        key = x.keys[i][0];
                        val = x.keys[i][1];
                        buffer.putInt(key);
                        buffer.putInt(val);
                    }
                }

                for (int i = 0; i < x.keys.length + 1 ; i++) {
                    buffer.putLong(x.childPointer[i]);
                }
                buffer.flip();
                fc.write(buffer);
                buffer.clear();
            }
        }
        public Node diskRead(long id)throws IOException {
            int key;
            int val;
            raf = new RandomAccessFile(file,"rw");
            raf.seek(id);
            fc = raf.getChannel();
            buffer = ByteBuffer.allocate(bufferCapacity);
            fc.read(buffer);
            buffer.flip();
            Node x;
            long nodeId = buffer.getLong();
            int bool = buffer.getInt();

            if(bool == 1)
                x = new Node(0, true);
            else
                x = new Node(0, false);
            x.nId = nodeId;

            for (int j = 0; j < x.keys.length; j ++) {
                key = buffer.getInt();
                val = buffer.getInt();
                if(key == 0){
                    x.keys[j][0] = 0;
                    x.keys[j][1] = 0;
                }
                else{
                    x.keys[j][0] = key;
                    x.keys[j][1] = val;
                    x.n++;
                }

            }
            if(bool == 1){
                for (int i = 0; i < x.keys.length + 1 ; i++) {
                    long ex = buffer.getLong();
                }
            }
            else{
                for (int i = 0; i < x.keys.length + 1; i++) {
                    x.childPointer[i] = buffer.getLong();
                }
            }
            buffer.clear();
            raf.close();
            fc.close();

            return x;
        }
        public int bTSearch(Node x, int key)throws IOException{
            int i = 0;

            while( i < x.n && x.keys[i][0] < key){
                i++;
            }
            if(i < x.n && x.keys[i][0] == key){
                return x.keys[i][1];
            }
            else
            if(x.leaf){
                return 0;
            }
            else{
                Node c;
                c = diskRead(x.childPointer[i]);
                return bTSearch(c,key);
            }
        }
        public void insertNonFull(Node x, int k) throws IOException, ClassNotFoundException {
            int kKey = k;
            boolean collision = false;
            for (int j = 0; j < x.keys.length ; j++) {
                if(x.keys[j][0] == kKey){
                    x.keys[j][1]++;
                    collision = true;
                    diskWrite(x);
                }
            }
            if(!collision) {
                int i = x.n - 1;
                if (x.leaf) {
                    while (i >= 0 && x.keys[i][0] > kKey) {
                        x.keys[i + 1][0] = x.keys[i][0];
                        x.keys[i + 1][1] = x.keys[i][1];
                        i--;
                    }
                    x.keys[i + 1][0] = k;
                    x.keys[i + 1][1] = 1;
                    x.n++;
                    diskWrite(x);
                }
                else {
                    while (i >= 0 && x.keys[i][0] > kKey)
                        i--;

                    i++;
                    Node child;
                    child = diskRead(x.childPointer[i]);
                    if (child.n == maxKeys) {
                        for (int j = 0; j < child.keys.length; j++) {
                            if (child.keys[j][0] == k) {
                                child.keys[j][1]++;
                                diskWrite(child);
                                collision = true;
                            }
                        }
                        // That child is full, so split it, and possibly
                        // update i to descend into the new child.
                        if (!collision) {
                            splitChild(child, i, x);
                            child = diskRead(x.childPointer[i + 1]);
                            if (x.keys[i][0] < kKey)
                                i++;
                        }
                    }
                    if (!collision)
                        insertNonFull(child, k);
                }
            }
        }


        public void splitChild(Node x, int i,Node p) throws IOException, ClassNotFoundException {

            Node parent = p;
            Node y = new Node(x.n,x.leaf);
            y.nId = x.nId;
            y.keys = x.keys;
            y.childPointer = x.childPointer;
            Node z = new Node(minDegree -1,y.leaf);
            z.nId = allocateNode(z);
            for (int j = 0; j < minDegree-1; j++) {
                z.keys[j][0] = y.keys[j+minDegree][0];
                z.keys[j][1] = y.keys[j+minDegree][1];
                y.keys[j+minDegree][0] = 0; // remove the reference
                y.keys[j+minDegree][1] = 0;

            }
            if (!y.leaf) {
                for (int j = 0; j < minDegree; j++) {
                    z.childPointer[j] = y.childPointer[j + minDegree]; // point of interest
                    y.childPointer[j + minDegree] = -1L; // remove the reference//
                }
            }
            y.n = minDegree-1;

            // Move the children in x that are to the right of y by
            // one position to the right.
            for (int j = parent.n; j >= i+1; j--) {
                parent.childPointer[j+1] = parent.childPointer[j];
            }

            // Drop z into x's child i+1.
            parent.childPointer[i+1] = z.nId;
            parent.childPointer[i] = y.nId;
            // Move the keys in x that are to the right of y by one
            // position to the right.
            for (int j = parent.n-1; j >= i; j--) {
                parent.keys[j + 1][0] = parent.keys[j][0];
                parent.keys[j+1][1] = parent.keys[j][1];
            }
            // Move this node's median key into x, and remove the
            // reference to the key in this node.
            parent.keys[i][0] = y.keys[minDegree-1][0];
            parent.keys[i][1] = y.keys[minDegree-1][1];
            y.keys[minDegree-1][0] = 0;
            y.keys[minDegree-1][1] = 0;

            parent.n++;		// one more key/child in x

            // All done.  Write out the nodes.
            diskWrite(y);
            diskWrite(z);
            diskWrite(parent);


        }
    }

}
