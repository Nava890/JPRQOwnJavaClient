package Server;

import config.Config;

import java.io.*;
import java.net.*;

class Main {
  public static final Config config = new Config();
  public static final Jprq jprq = new Jprq();

  public static void main(String[] args) {
    if (!config.load()) {
      System.out.println("unable to load config");
      System.exit(1);
    }
    jprq.init(config);
    Thread taskThread = new Thread(() -> {
      try {
        while (!Thread.currentThread().isInterrupted()) {
          try {
            jprq.start();
            Thread.sleep(1000); // Simulate work
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle the interruption
          }
        }
      } catch (Exception e) {
        System.err.println("Error occurred: " + e.getMessage());
      } finally {
        // Ensure clean shutdown (equivalent to defer jprq.Stop())
        System.out.println("Task stopped.");
      }
    });

    // Add a shutdown hook to handle interrupts (like SIGINT)
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("Signal received, stopping task...");
      taskThread.interrupt(); // Interrupt the task thread to stop gracefully
    }));

    // Start the task thread
    taskThread.start();

    try {
      // Wait for the task to finish (or for interruption to stop)
      taskThread.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // Handle any interruption
    }

    System.out.println("Program terminated.");
  }

}
