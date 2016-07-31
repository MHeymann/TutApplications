/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet;

import java.io.Serializable;

/**
 *
 * @author 15988694
 */
public class Packet implements Serializable {
    
    public int code;
    public String data;

    
    public Packet(int code, String data) {
        this.code = code;
        this.data = data;
    }
    

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
