/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datacompression;



/**
 *
 * @author nEW u
 */
public class Huffman
{
    private char character;
    private long frequency;
    private Huffman rChild, lChild;
    private String code;
    
    Huffman()
    {;}
    Huffman(char c,int f)
    {
        character=c;
        frequency=f;
        rChild=lChild=null;
    }
    
    public Huffman create(Huffman a,Huffman b)
    {
        this.frequency=a.getFrequency()+b.getFrequency();
        
        this.character='-';
        this.lChild=a;
        this.rChild=b;
        return this;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public Huffman getrChild() {
        return rChild;
    }

    public void setrChild(Huffman rChild) {
        this.rChild = rChild;
    }

    public Huffman getlChild() {
        return lChild;
    }

    public void setlChild(Huffman lChild) {
        this.lChild = lChild;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
}
    
    
   
    
    
    
    
    

