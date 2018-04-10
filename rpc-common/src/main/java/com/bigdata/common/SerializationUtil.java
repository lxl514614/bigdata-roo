package com.bigdata.common;

import java.io.*;

public class SerializationUtil {

    /**
     * 序列化
     * @param data
     * @param genericClass
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object serialize(byte[] data, Class<?> genericClass) throws IOException, ClassNotFoundException {

        ByteArrayOutputStream saos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(saos);
        oos.writeObject(genericClass);
        oos.flush();
        return saos.toByteArray();
    }

    /**
     * 反序列化
     * @param data
     * @param genericClass
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deserialize(byte[] data, Class<?> genericClass) throws IOException, ClassNotFoundException {
        ByteArrayInputStream sais=new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(sais);
        genericClass = (Class<?>) ois.readObject();
        return genericClass;
    }


}
