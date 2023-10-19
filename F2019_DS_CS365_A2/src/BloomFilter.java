
import java.util.BitSet;

public class BloomFilter {

    int m;
    int hash1,hash2,hash3;
    FNVHash fnvHash;
    MurmurHash mHash;

    public BloomFilter(int m){
        this.m = m;
        hash1 = 0;
        hash2 = 0;
        hash3 = 0;
        fnvHash = new FNVHash();
        mHash = new MurmurHash();
    }

    private int getHashCode1(String k){
        hash1 = Math.abs(k.hashCode() % m);
        return hash1;
    }
    private int getHashCode2(String k){
        hash2 = Math.abs(mHash.hash32(k) % m);
        return hash2;
    }
    private int getHashCode3(String k){
        hash3 = Math.abs(fnvHash.hash32(k) % m);
        return hash3;
    }

    public int orHashBits(String k){

        BitSet a = new BitSet(32);
        BitSet b = new BitSet(32);
        BitSet c = new BitSet(32);
        BitSet d = new BitSet(64);
        BitSet e = new BitSet(96);
        a.set(getHashCode1(k));
        b.set(getHashCode2(k));
        c.set(getHashCode3(k));
        a.or(b);
        d = a;
        d.or(c);
        e = d;

        String result = e.toString();
        result = result.replace(",","");
        result = result.replace("{","");
        result = result.replace("}","");
        result = result.replace(" ", "");
        return Integer.parseInt(result);
    }


}
