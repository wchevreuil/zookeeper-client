package com.cloudera.zookeeper.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

public class SimpleACLsEditor {

  private static final Id WORLD_ANYONE = new Id();

  private static final ACL ACL_PUBLIC = new ACL();

  static {

    WORLD_ANYONE.setId("anyone");

    WORLD_ANYONE.setScheme("world");

    ACL_PUBLIC.setId(WORLD_ANYONE);

    ACL_PUBLIC.setPerms(31);
  }
  /**
   * @param args
   * @throws IOException
   * @throws InterruptedException
   * @throws KeeperException
   */
  public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

    if (args.length != 3) throw new IllegalArgumentException();

    SimpleACLsEditor aclsEditor = new SimpleACLsEditor();

    ZooKeeper zk = new ZooKeeper(args[0], 15000, new Watcher() {
      
      public void process(WatchedEvent event) {
        // TODO Auto-generated method stub

      }
    });
    
    zk.addAuthInfo("digest", "super:cloudera".getBytes());

    if (args[1].equals("listACLs")) {

      aclsEditor.recursivelyListACLs(zk, args[2]);

    } else if (args[1].equals("setACLsToPublic")) {

      aclsEditor.recursivelySetACLs(zk, args[2]);

    }
    
  }

  public void recursivelySetACLs(ZooKeeper zk, String path) throws KeeperException,
      InterruptedException {

    List<String> children = zk.getChildren(path, false);

    if (children != null && children.size() > 0) {

      for (String child : children) {

        String newPath = path + "/" + child;

        System.out.println("Setting ACL 'world,'anyone:cdrwa to " + path);

        List<ACL> acls = new ArrayList<ACL>();

        acls.add(ACL_PUBLIC);

        Stat meta = new Stat();

        zk.getData(newPath, false, meta);

        zk.setACL(newPath, acls, meta.getAversion());

        System.out.println("---------");

        recursivelySetACLs(zk, newPath);

      }

    }

  }

  public void recursivelyListACLs(ZooKeeper zk, String path) throws KeeperException,
      InterruptedException {

    List<String> children = zk.getChildren(path, false);

    if (children != null && children.size() > 0) {

      for (String child : children) {

        String newPath = path + "/" + child;

        System.out.println("ACLs to: " + newPath);

        List<ACL> acls = zk.getACL(newPath, new Stat());

        for (ACL acl : acls) {

          System.out.println("Perms: " + acl.getPerms());

          System.out.println("Acl Id: " + acl.getId());

          System.out.println("------------");

        }

        Stat meta = new Stat();

        byte[] data = zk.getData(path, false, meta);

        System.out.println(path);

        System.out.println("metadata for " + path + ": " + meta);

        System.out.println("Aversion: " + meta.getAversion() + " - Version: " + meta.getVersion());

        recursivelyListACLs(zk, newPath);

      }

    }

  }

}
