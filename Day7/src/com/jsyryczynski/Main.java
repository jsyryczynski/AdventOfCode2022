package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static final int FULL_DISK_SIZE = 70000000;

    public static void main(String[] args) {


        try (Scanner scanner = new Scanner(new File("input.txt"))) {
            TreeNode rootNode = new TreeNode("/",null);
            TreeNode currentNode = rootNode;
            HashMap<String, TreeNode> childrenList = null;

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                System.out.println(line);
                if (line.substring(0,1).equals("$")) {
                    childrenList = moveChildrenToCurrentNode(currentNode, childrenList);
                    if (line.substring(2,4).equals("ls")) {
                        System.out.println("ls command");
                        childrenList = new HashMap<>();
                    }
                    else {
                        String[] words = line.split("\s");
                        String dest = words[2];
                        if (dest.equals("..")) {
                            System.out.println("cd up");
                            currentNode = currentNode.parent;
                        }
                        else if (dest.equals("/")) {
                            System.out.println("cd root");
                            currentNode = rootNode;
                        }
                        else {
                            System.out.println("cd dirname " + dest);
                            currentNode = currentNode.children.get(dest);
                        }
                    }
                }
                else {
                    if (line.substring(0,3).equals("dir")) {
                        String dirName = line.substring(4,line.length());
                        System.out.println("directory named " + dirName);
                        childrenList.put(dirName, new TreeNode(dirName, currentNode));
                    }
                    else {
                        String[] words = line.split("\s");
                        String fileSize = words[0];
                        String fileName = words[1];
                        System.out.println("file " + fileName + " size " + fileSize);
                        childrenList.put(fileName, new TreeNode(fileName, currentNode, Long.parseLong(fileSize)));
                    }
                }
            }
            moveChildrenToCurrentNode(currentNode, childrenList);

            calculateDirSize(rootNode);
            long currentFreeSpace = 70000000L - rootNode.size;
            long spaceToFree = 30000000L - currentFreeSpace;

            long result = iterateAndFindResult(rootNode, spaceToFree);
            System.out.println("result " + result);

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    private static long iterateAndFindResult(TreeNode node, long spaceToFree) {
        if (node.isFile) {
            return FULL_DISK_SIZE;
        }

        long minSizeToFree = FULL_DISK_SIZE;
        if (node.size >= spaceToFree) {
            minSizeToFree = node.size;
        }
        for (TreeNode value : node.children.values()) {
            long candidate = iterateAndFindResult(value, spaceToFree);
            if (candidate < minSizeToFree) {
                minSizeToFree = candidate;
            }
        }

        return minSizeToFree;
    }

    private static long calculateDirSize(TreeNode node) {
        if (node.isFile) {
            return node.size;
        }

        long sum = 0;
        for (TreeNode value : node.children.values()) {
            sum += calculateDirSize(value);
        }
        node.size = sum;
        return sum;
    }

    private static HashMap<String, TreeNode> moveChildrenToCurrentNode(TreeNode currentNode,
            HashMap<String, TreeNode> childrenList) {
        if (currentNode.children.isEmpty() && childrenList != null) {
            currentNode.children = childrenList;
        }
        return null;
    }
}

class TreeNode {
    long size;
    final String name;
    final boolean isFile;
    HashMap<String, TreeNode> children = new HashMap<>();
    final TreeNode parent;

    TreeNode(String name, TreeNode parent) {
        this.name = name;
        this.isFile = false;
        this.parent = parent;
    }

    TreeNode(String name, TreeNode parent, long size) {
        this.name = name;
        this.isFile = true;
        this.size = size;
        this.parent = parent;
    }
}
