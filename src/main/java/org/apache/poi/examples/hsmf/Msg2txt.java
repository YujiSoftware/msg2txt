/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.examples.hsmf;

import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import picocli.CommandLine;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Reads one or several Outlook MSG files and for each of them creates
 * a text file from available chunks and a directory that contains
 * attachments.
 */
@CommandLine.Command(name = "msg2txt")
public class Msg2txt implements Callable<Integer> {

    @CommandLine.Option(names = {"-h", "--help", "/?"}, description = "show this help", usageHelp = true)
    boolean showHelp;

    @CommandLine.Option(names = {"-o", "--output", "/o"}, description = "output directory")
    Path outputDir;

    @CommandLine.Option(names = {"-t", "--text-only", "/t"}, description = "extract text only (not extract attachments)", defaultValue = "false")
    boolean textOnly;

    @CommandLine.Parameters(paramLabel = "FILE")
    private List<Path> paths;

    @Override
    public Integer call() {
        boolean ok = true;
        for (Path path : paths) {
            String fileName = path.toString();
            try {
                processMessage(fileName);
            } catch (IOException e) {
                System.err.println("Could not process " + fileName + ": " + e);
                ok = false;
            }
        }

        return ok ? 0 : 1;
    }

    /**
     * Processes the message.
     *
     * @param fileName file name
     * @throws IOException if an exception occurs while writing the message out
     */
    private void processMessage(String fileName) throws IOException {
        MAPIMessage msg = new MAPIMessage(fileName);

        String name = fileName;
        if (fileName.endsWith(".msg") || fileName.endsWith(".MSG")) {
            name = fileName.substring(0, fileName.length() - 4);
        }

        String txtFileName = name + ".txt";
        String attDirName = name + "-att";
        if (outputDir != null) {
            txtFileName = outputDir.resolve(Path.of(txtFileName).getFileName()).toString();
            attDirName = outputDir.resolve(Path.of(attDirName).getFileName()).toString();
        }

        try (PrintWriter txtOut = new PrintWriter(txtFileName, "UTF-8")) {
            try {
                String displayFrom = msg.getDisplayFrom();
                txtOut.println("From: " + displayFrom);
            } catch (ChunkNotFoundException e) {
                // ignore
            }
            try {
                String displayTo = msg.getDisplayTo();
                txtOut.println("To: " + displayTo);
            } catch (ChunkNotFoundException e) {
                // ignore
            }
            try {
                String displayCC = msg.getDisplayCC();
                txtOut.println("CC: " + displayCC);
            } catch (ChunkNotFoundException e) {
                // ignore
            }
            try {
                String displayBCC = msg.getDisplayBCC();
                txtOut.println("BCC: " + displayBCC);
            } catch (ChunkNotFoundException e) {
                // ignore
            }
            try {
                String subject = msg.getSubject();
                txtOut.println("Subject: " + subject);
            } catch (ChunkNotFoundException e) {
                // ignore
            }
            try {
                String body = msg.getTextBody();
                txtOut.println(body);
            } catch (ChunkNotFoundException e) {
                System.err.println("No message body");
            }

            if (!textOnly) {
                AttachmentChunks[] attachments = msg.getAttachmentFiles();
                if (attachments.length > 0) {
                    File d = new File(attDirName);
                    if (d.mkdir()) {
                        for (AttachmentChunks attachment : attachments) {
                            processAttachment(attachment, d);
                        }
                    } else {
                        System.err.println("Can't create directory " + attDirName);
                    }
                }
            }
        }
    }

    /**
     * Processes a single attachment: reads it from the Outlook MSG file and
     * writes it to disk as an individual file.
     *
     * @param attachment the chunk group describing the attachment
     * @param dir        the directory in which to write the attachment file
     * @throws IOException when any of the file operations fails
     */
    private void processAttachment(AttachmentChunks attachment,
                                   File dir) throws IOException {
        String fileName = attachment.getAttachFileName().toString();
        if (attachment.getAttachLongFileName() != null) {
            fileName = attachment.getAttachLongFileName().toString();
        }

        File f = new File(dir, fileName);
        try (OutputStream fileOut = new FileOutputStream(f)) {
            fileOut.write(attachment.getAttachData().getValue());
        }
    }

    /**
     * Processes the list of arguments as a list of names of Outlook MSG files.
     *
     * @param args the list of MSG files to process
     */
    public static void main(String[] args) {
        System.exit(new CommandLine(new Msg2txt()).execute(args));
    }
}

