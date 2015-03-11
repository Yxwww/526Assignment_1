/**
 * Created by Yuxibro on 15-02-28.
 */

import java.util.*;

public class EncryptionEncapsulate {
    public String originalFileName;
    public String encryptionType;
    public String encryptedFileName;
    public String decryptedFileName;
    public LinkedList<byte[]> blockList;
    public String report;
    public int totalIdenticalBlock;
    public EncryptionEncapsulate(){
        this.originalFileName = "";
        this.encryptionType ="";
        this.encryptedFileName = "";
        this.decryptedFileName = "";
        this.report = "";
        this.blockList = new LinkedList<byte[]>();
        this.totalIdenticalBlock = 0;
    }
    public EncryptionEncapsulate(String ori, String enc, String dec, LinkedList<byte[]> bl){
        this.originalFileName = ori;
        this.encryptedFileName = enc;
        this.decryptedFileName = dec;
        this.blockList = bl;
    }
    public String toString(){
        String blockListToString = "";
        for (byte[] bytes : this.blockList) {
            // do something
            blockListToString += new String(bytes);
            //System.out.println(new String(bytes));
        }
        if (this.report.length()==0){
            composeReport();
        }
        return "Origin File: "+this.originalFileName+//" encrypted as: "+this.encryptedFileName+" decrypted as: "+this.decryptedFileName+
                "\n"+ this.report;
    }
    public void composeReport(){
        byte[] tempBlock,indexBlock;
        int indexIdenticalBlockSum;
        LinkedList<byte[]> dupBlockList = this.blockList;
        if (this.report.length()!=0){
            return;
        }
        this.report += "\tFile : "+this.originalFileName + " encrypted by "+this.encryptionType+"\n\t# of Blocks: "+this.blockList.size();

        for(int i = 0; i<dupBlockList.size(); i++){
            indexBlock = dupBlockList.get(i);
            indexIdenticalBlockSum = 0;
            for(int j=i+1;j<dupBlockList.size(); j++){
                tempBlock = dupBlockList.get(j);
                if(Arrays.equals(indexBlock,tempBlock)){// If two array is identical
                    this.report+="\n\t -> detect identical block "+ " on index: "+j + " for block "+ i;
                    indexIdenticalBlockSum++;           // Increment sum
                    dupBlockList.remove(j);             // remove the tempBlock from list if it's detected as identical
                }
            }
            if(indexIdenticalBlockSum>0)
                this.report+="\n\tFound "+indexIdenticalBlockSum+ " identical block for a block.";
            this.totalIdenticalBlock += indexIdenticalBlockSum;
        }
        this.report+="\n\tTotal identical blocks: "+this.totalIdenticalBlock;
        this.report+="\nEND.\n\n";
    }
}
