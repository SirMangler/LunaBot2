package lunabot.ai;

import java.util.AbstractList;

import net.dv8tion.jda.core.entities.Member;

/**
 * @author SirMangler
 *
 * @date 26 Feb 2019
 */
public class SpeakerList extends AbstractList<Speaker> {

	
	private Speaker[] list = new Speaker[10];
    private int size = 0;


    public Speaker get(int i){
        if(i >= size) throw new IndexOutOfBoundsException("duh!");
        return list[i];
    }
    
    public Speaker get(String name){
       for (Speaker speaker : list) {
    	  if (speaker.name.equalsIgnoreCase(name)) return speaker;
       };
       
       Speaker speaker = new Speaker(name);
       add(speaker);
       
       return speaker;
    }
    
    public Speaker get(Member member){
        for (Speaker speaker : list) {
     	  if (speaker.name.equalsIgnoreCase(member.getUser().getName())) return speaker;
        };
        
        Speaker speaker = new Speaker(member.getUser().getName());
        speaker.mention=member.getAsMention();
        add(speaker);
        
        return speaker;
     }

    public boolean add(Speaker e){
        if(size >= list.length){
            Speaker[] newList = new Speaker[list.length + 10];
            System.arraycopy(list,0, newList, 0, list.length);
            list = newList;
        }
        
        list[size] = e;
        size++;
        return true;
    }

    public int size(){
        return size;
    }

}
