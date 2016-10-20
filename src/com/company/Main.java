package com.company;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final String USAGE = " USAGE: [I | D] [IP_Address | Domain_Name]:port";
    private static final String FILE_PATH = "nums.txt";

    private static boolean isDomain;
    private static String domain;
    private static String ip;
    private static short port;

    private static void testConversions() {

        int num = 1;

        byte[] bytes = ConversionUtils.translateToNetwork(num);

        assert(bytes[0] == 1);
        for(int i = 1; i < 4; ++i) {
            assert(bytes[i] == 0);
        }

        num = ConversionUtils.translateToHost(bytes);
        assert(num == 1);

        num = 1025;

        bytes = ConversionUtils.translateToNetwork(num);

        assert(bytes[0] == 0);
        assert(bytes[1] == 0);
        assert(bytes[2] == 4);
        assert(bytes[3] == 1);

        num = ConversionUtils.translateToHost(bytes);

        assert(num == 1025);
    }

    private static void parseArgs(List<String> args) throws InvalidArgumentException {

        List<String> options = args.stream().filter((arg) -> (arg.matches("-."))).collect(Collectors.toList());

        // We can't have both -I and -D
        if(options.stream().anyMatch((arg) -> (arg.matches("-I"))) &&
                options.stream().anyMatch((arg) -> (arg.matches("-D")))) {
            throw new InvalidArgumentException(new String[]{USAGE});
        }

        isDomain = false;

        int index = options.indexOf("-I");

        // We MUST have either -I or -D
        if(index == -1) {
            index = options.indexOf("-D");
            if(index == -1) {
                throw new InvalidArgumentException(new String[]{USAGE});
            }
            isDomain = true;
        }

        List<String> pairs = args.stream().filter((arg) -> (arg.matches(".*:.*"))).collect(Collectors.toList());

        // Only one pair is needed. More would lead to undefined behaviour. Better warn the user
        if(pairs.size() == 0 || pairs.size() > 1) {
            throw new InvalidArgumentException(new String[]{"Too many pairs\n", USAGE});
        }

        String[] pairSplit = pairs.get(0).split(":");
        if(isDomain) {
            domain = pairSplit[0];
        } else {
            ip = pairSplit[0];
        }

        try {
            port = Short.parseShort(pairSplit[1]);
        }catch(NumberFormatException e) {
            throw new InvalidArgumentException(new String[]{"Invalid port number entered\n", USAGE});
        }
    }

    public static void main(String[] args) {
        // write your code here

        //testConversions();

        List<String> lines = null;
        int[] numbers;

        try {
            parseArgs(Stream.of(args).collect(Collectors.toList()));
        } catch(InvalidArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(3);
        }

        try {
            lines = Files.readAllLines(Paths.get(FILE_PATH));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        numbers = lines.stream()
                .mapToInt(Integer::parseInt).toArray();
        Client c = new Client();
        try {

            if(isDomain) {
                c.setAddress(new Domain(domain));
            } else {
                c.setAddress(new IP(ip));
            }

            c.setPort(port);
            int result = c.run(numbers);

            System.out.println("Result: " + result);

        } catch (UnknownHostException e) {
            System.err.println("Invalid host");
            System.exit(2);
        } catch (InvalidArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
