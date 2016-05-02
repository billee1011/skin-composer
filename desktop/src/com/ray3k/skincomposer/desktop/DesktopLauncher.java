package com.ray3k.skincomposer.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.CloseListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.DesktopWorker;
import com.ray3k.skincomposer.FilesDroppedListener;
import com.ray3k.skincomposer.utils.Utils;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

public class DesktopLauncher implements DesktopWorker, Lwjgl3WindowListener {
    private Array<FilesDroppedListener> filesDroppedListeners;
    private CloseListener closeListener;
    
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setResizable(true);
        config.useVsync(true);
        config.setWindowedMode(800, 800);
        DesktopLauncher desktopLauncher = new DesktopLauncher();
        config.setWindowListener(desktopLauncher);
        config.setTitle("Skin Composer - New Project*");
        Main main = new Main();
        main.setTextureWorker(desktopLauncher);
        
        try {
            new Lwjgl3Application(main, config);
        } catch (Exception e) {
            e.printStackTrace();
            
            FileWriter fw = null;
            try {
                fw = new FileWriter(Gdx.files.local("temp/test.txt").file(), true);
                PrintWriter pw = new PrintWriter(fw);
                e.printStackTrace(pw);
                pw.close();
                fw.close();
                int choice = JOptionPane.showConfirmDialog(null, "Exception occurred. See error log?", "Skin Composer Exception!", JOptionPane.YES_NO_OPTION);
                if (choice == 0) {
                    Utils.openFileExplorer(Gdx.files.local("temp/test.txt"));
                }
            } catch (Exception ex) {

            }
        }
    }

    public DesktopLauncher() {
        filesDroppedListeners = new Array<>();
    }
    
    @Override
    public void texturePack(Array<FileHandle> handles, FileHandle targetFile, int maxWidth, int maxHeight) {
        Settings settings = new TexturePacker.Settings();
        settings.maxWidth = maxWidth;
        settings.maxHeight = maxHeight;
        settings.duplicatePadding = true;
        settings.square = true;
        settings.filterMin = Texture.TextureFilter.Linear;
        settings.filterMag = Texture.TextureFilter.Linear;
        settings.fast = true;
        settings.useIndexes = false;
        settings.silent = true;
        settings.flattenPaths = true;
        TexturePacker p = new TexturePacker(settings);
        for (FileHandle handle : handles) {
            p.addImage(handle.file());
        }
        p.pack(targetFile.parent().file(), targetFile.nameWithoutExtension());
    }
    
    @Override
    public void centerWindow(Graphics graphics) {
        Lwjgl3Graphics g = (Lwjgl3Graphics) graphics;
        Graphics.DisplayMode mode = g.getDisplayMode();
        Lwjgl3Window window = g.getWindow();
        window.setPosition(mode.width / 2 - g.getWidth() / 2, mode.height / 2 - g.getHeight() / 2);
    }

    @Override
    public void sizeWindowToFit(int maxWidth, int maxHeight, int displayBorder, Graphics graphics) {
        Graphics.DisplayMode mode = graphics.getDisplayMode();
        
        int width = Math.min(mode.width - displayBorder * 2, maxWidth);
        int height = Math.min(mode.height - displayBorder * 2, maxHeight);
        
        graphics.setWindowedMode(width, height);
        
        centerWindow(graphics);
    }

    @Override
    public void iconified() {
        
    }

    @Override
    public void deiconified() {
        
    }

    @Override
    public void focusLost() {
        
    }

    @Override
    public void focusGained() {
        
    }

    @Override
    public boolean closeRequested() {
        if (closeListener != null) {
            return closeListener.closed();
        } else {
            return true;
        }
    }
    
    @Override
    public void addFilesDroppedListener(FilesDroppedListener filesDroppedListener) {
        filesDroppedListeners.add(filesDroppedListener);
    }
    
    @Override
    public void removeFilesDroppedListener(FilesDroppedListener filesDroppedListener) {
        filesDroppedListeners.removeValue(filesDroppedListener, false);
    }
    
    @Override
    public void filesDropped(String[] files) {
        Array<FileHandle> fileHandles = new Array<>();
        for (String file : files) {
            FileHandle fileHandle = new FileHandle(file);
            fileHandles.add(fileHandle);
        }
        
        for (FilesDroppedListener listener : filesDroppedListeners) {
            listener.filesDropped(fileHandles);
        }
    }

    @Override
    public void setCloseListener(CloseListener closeListener) {
        this.closeListener = closeListener;
    }
}