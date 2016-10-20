package com.company;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by AlexandruD on 10/13/2016.
 */
public class Client {

    private InetAddress address;
    private short port;

    public Client() {
        address = null;
        port = -1;
    }

    public void setAddress(IP ip) throws UnknownHostException {
        address = InetAddress.getByAddress(ip.getIp().getBytes());
    }

    public void setAddress(Domain domain) throws UnknownHostException {
        address = InetAddress.getByName(domain.getDomain());
    }

    public void setPort(short port) {
        this.port = port;
    }

    public int run(int[] numbers) throws UnknownHostException, InvalidArgumentException {

        if(port == -1) {
            throw new InvalidArgumentException(new String[]{"Port has not been set"});
        }

        if(address == null) {
            throw new InvalidArgumentException(new String[]{"Address has not been set"});
        }

        int result = 0;

        try(Socket socket = new Socket(address, port)) {

            OutputStream out = socket.getOutputStream();

            int index = 0;
            byte[] toWrite = new byte[(numbers.length + 1) * 4];

            byte[] numBytes = ConversionUtils.translateToNetwork(numbers.length);

            for(byte b : numBytes) {
                toWrite[index++] = b;
            }

            for(int num : numbers) {
                numBytes = ConversionUtils.translateToNetwork(num);
                for(byte b : numBytes) {
                    toWrite[index++] = b;
                }
            }

            out.write(toWrite, 0, (numbers.length + 1) * 4);

            InputStream in = socket.getInputStream();

            byte[] resBytes = new byte[4];

            int count = in.read(resBytes, 0, 4);

            in.close();

            if(count < 4) {
                throw new IOException("Result not read entirely");
            }

            result = ConversionUtils.translateToHost(resBytes);

        } catch(IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
