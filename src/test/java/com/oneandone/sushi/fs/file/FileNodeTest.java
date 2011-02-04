/*
 * Copyright 1&1 Internet AG, http://www.1and1.org
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.oneandone.sushi.fs.file;

import com.oneandone.sushi.fs.Filesystem;
import com.oneandone.sushi.fs.Node;
import com.oneandone.sushi.fs.NodeTest;
import com.oneandone.sushi.io.OS;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/** TODO: move more tests into NodeTest */
public class FileNodeTest extends NodeTest {
    @Override
    protected Node createWork() throws IOException {
        return IO.getTemp().createTempDirectory();
    }

    @Test
    public void fileConstructor() {
        assertEquals(IO.getHome(), IO.file(System.getProperty("user.home")));
        assertEquals(work.getPath(), IO.file(work.getAbsolute() + "/").getPath());
        assertEquals(work.getPath(), IO.file(new File(work.getAbsolute() + "/")).getPath());
        assertEquals("", IO.file("/").getPath());
    }

    @Test
    public void tailingSlash() throws Exception {
        Filesystem fs;
        Node node;

        fs = work.getRoot().getFilesystem();
        assertTrue(work.getURI().toString().endsWith(fs.getSeparator()));
        node = work.getIO().node(fs.getScheme() + ":" + work.getRoot().getId() + work.getPath() + fs.getSeparator());
        assertFalse(node.getPath().endsWith(fs.getSeparator()));
    }


    @Test
    public void renameFile() throws IOException {
        FileNode src;
        FileNode dest;

        src = (FileNode) work.join("src");
        dest = (FileNode) work.join("dest");
        try {
            src.rename(dest);
            fail();
        } catch (FileNotFoundException e) {
            // ok
        }
        src.mkfile();
        src.rename(dest);
        assertFalse(src.exists());
        assertTrue(dest.exists());
        src.mkfile();
        try {
            dest.rename(src);
            fail();
        } catch (IOException e) {
            // ok
        }
    }

    @Test
    public void mkfile() throws IOException {
        FileNode file;

        file = (FileNode) work.join("mkfile");
        assertFalse(file.exists());
        file.mkfile();
        assertTrue(file.exists());
        assertTrue(file.isFile());
        try {
            file.mkfile();
            fail();
        } catch (IOException e) {
            // ok
        }
        try {
            file.mkdir();
            fail();
        } catch (IOException e) {
            // ok
        }
        file.delete();
    }

    @Test
    public void modeFile() throws IOException {
        checkMode(IO.getTemp().createTempFile());
    }

    @Test
    public void modeDir() throws IOException {
        checkMode(IO.getTemp().createTempDirectory());
    }

    private void checkMode(FileNode node) throws IOException {
        if (node.getIO().os == OS.WINDOWS) {
            return; // TODO
        }
        checkMode(node, 0644);
        checkMode(node, 0700);
        assertTrue(node.getFile().canRead());
        assertTrue(node.getFile().canWrite());
        checkMode(node, 0000);
        assertFalse(node.getFile().canRead());
        assertFalse(node.getFile().canWrite());
        checkMode(node, 0777);
        assertTrue(node.getFile().canRead());
        assertTrue(node.getFile().canWrite());
    }

    private void checkMode(FileNode node, int mode) throws IOException {
        node.setMode(mode);
        assertEquals(mode, node.getMode());
    }

    //--

    @Test
    public void filesystem() {
        assertEquals(File.separator, work.getRoot().getFilesystem().getSeparator());
        assertEquals(File.separatorChar, work.getRoot().getFilesystem().getSeparatorChar());
    }

    @Test
    public void temp() throws IOException {
        FileNode tmp;

        tmp = ((FileNode) work).createTempFile();
        assertEquals("", tmp.readString());
        tmp = ((FileNode) work).createTempDirectory();
        assertEquals(0, tmp.list().size());
    }
}
