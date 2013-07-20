/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.missatgeria;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 *
 * @author Josep
 */
public class LimitDocument extends PlainDocument {
    private int limit;


    public LimitDocument(int i) {
        super();
        this.limit = i;
    }

    @Override
    public void insertString(int offset, String s, AttributeSet attributeSet)
            throws BadLocationException
    {
        int words = super.getLength();
        if(words<limit)
        {
            super.insertString(offset, s, attributeSet);
        }
    }
    /**
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

   
    
}
