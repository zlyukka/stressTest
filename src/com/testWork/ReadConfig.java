package com.testWork;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.ini4j.*;



/**
 * Created by Виталик on 28.07.2018.
 */
class ReadConfig {
    Ini ini=null;

    public <T> T getResultByName(String name, Class<T> clazz) throws IllegalArgumentException{
        T element = ini.get("FIRST", name, clazz);
        return element;

    }

    public boolean getConfigFile(){
        boolean result = true;
        File file = new File(getPathToConfigFile()+File.separator+Constant.CONFIG_FILE_NAME);
        if(file.exists()){
            System.out.println("file "+file.getPath()+" is exist");
            try {
                readConfig(file);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else{
            System.out.println("file "+file.getPath()+" does not exist");
            try {
                saveFile(file);
            } catch (IOException e) {
                e.printStackTrace();
                result=false;
            }
        }
        return true;
    }

    private void readConfig(File file) throws IOException {
        ini = new Ini(file);
    }

    private void saveFile(File file) throws IOException {
        System.out.println("Create file " + file);
        file.createNewFile();
        Ini ini = new Ini(file);
        ini.put("FIRST", "url",Constant.URL);
        ini.put("FIRST", "requestPerSecond", Constant.REQUEST_PER_SECOND);
        ini.put("FIRST", "duration", Constant.DURATION);
        ini.store();
        readConfig(file);
    }

    private String getPathToConfigFile(){
        String result = "";
        try {
            result = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReadConfig that = (ReadConfig) o;

        return ini != null ? ini.equals(that.ini) : that.ini == null;

    }

    @Override
    public int hashCode() {
        return ini != null ? ini.hashCode() : 0;
    }
}
