/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.missatgeria;

import java.util.HashMap;
import org.iesapp.framework.pluggable.daemons.TopModuleDaemon;

/**
 *
 * @author Josep
 */
public class MissatgeriaDeamon extends org.iesapp.framework.pluggable.daemons.TopModuleDaemon {

    private int solPendents = 0;
    private String message;
    @Override
    protected void checkStatusProcedure() {
       if(coreCfg.getUserInfo()!=null)
       {
            int solPendentsNew = coreCfg.getIesClient().getMissatgeriaCollection().getNumSolPendents();
            if(solPendentsNew>0)
            {
                 message = "Teniu "+solPendentsNew+" sol·licituds pendents.";
                 status = TopModuleDaemon.STATUS_AWAKE;
            }
            else
            {
                 message = "";
                 status = TopModuleDaemon.STATUS_NORMAL;
            }

            if(solPendents!=solPendentsNew)
            {
           // System.out.println("Fire...for "+coreCfg.getUserInfo().getAbrev()+": "+"solpendentes"+ solPendents+""+ solPendentsNew);
                 this.propertyChangeSupport.firePropertyChange("solpendentes", solPendents, solPendentsNew);
            }
            solPendents = solPendentsNew;
       }
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void reset() {
       message = "";
       int newsolPendents = 0;
       status = TopModuleDaemon.STATUS_NORMAL;
       this.propertyChangeSupport.firePropertyChange("solpendents",newsolPendents,solPendents);
       solPendents = 0;
       
    }

   @Override
    public HashMap getCurrentValues() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        return map;
    }
   
}
