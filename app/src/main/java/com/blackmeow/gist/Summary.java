package com.blackmeow.gist.activities;

import java.util.Arrays;
import java.util.Comparator;

public class Summary {
    //s is the text(converted from speech)
    Sentence[] SummarisedSentences;

    Summary(String[] keywords,String s){
        //removing spaces from keywords
        String keys[]=new String[keywords.length];
        for(int i=0;i<keys.length;i++)
        {
            String temp="";
            for(int j=0;j<keywords[i].length();j++)
            {
                char x=keywords[i].charAt(j);
                if(x!=32)
                {
                    temp=temp+x;
                }
            }
            keys[i]=temp;
        }

        String arr[]=s.split("period");
        Sentence sent[]=new Sentence[arr.length];
        for(int i=0;i<sent.length;i++)
        {
            Sentence se=new Sentence(arr[i],i);
            sent[i]=se;
        }

        for(int i=0;i<sent.length;i++)
        {
            int score=0;
            for(int j=0;j<keys.length;j++)
            {
                boolean flag=sent[i].sen.toLowerCase().contains(keys[j].toLowerCase());
                if(flag==true)
                {
                    score++;
                }
            }
            sent[i].score=score;
            //[i].display();
        }
        Arrays.sort(sent, new SentCompScore());
        for(int i=0;i<sent.length;i++)
        {
            //sent[i].display();
        }
        //user input for % summarized
        Sentence summarized[]=new Sentence[sent.length/2];
        for(int i=0;i<summarized.length;i++)
        {
            Sentence se=new Sentence(sent[i].normal,sent[i].pos);
            summarized[i]=se;
        }
        Arrays.sort(summarized, new SentCompPos());

        SummarisedSentences=summarized;
    }

    Sentence[] getSentences(){
        return SummarisedSentences;
    }
}

class SentCompScore implements Comparator<Sentence>
{
    public int compare(Sentence s1, Sentence s2)
    {
        return (s2.score - s1.score);
    }
}

class SentCompPos implements Comparator<Sentence>
{
    public int compare(Sentence s1, Sentence s2)
    {
        return (s1.pos - s2.pos);
    }
}