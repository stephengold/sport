/*
 Copyright (c) 2022-2025 Stephen Gold and Yanis Boudiaf

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. Neither the name of the copyright holder nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.stephengold.sport.demo;

import com.github.stephengold.sport.BaseApplication;
import com.github.stephengold.sport.test.AssimpTest;
import com.github.stephengold.sport.test.CheckerboardTest;
import com.github.stephengold.sport.test.ClipspaceTest;
import com.github.stephengold.sport.test.DynamicMeshTest;
import com.github.stephengold.sport.test.IcosphereTest;
import com.github.stephengold.sport.test.MouseTest;
import com.github.stephengold.sport.test.MouseTest2;
import com.github.stephengold.sport.test.OctasphereTest;
import com.github.stephengold.sport.test.RainbowTest;
import com.github.stephengold.sport.test.SpriteTest;
import com.github.stephengold.sport.test.TextureTest;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * Choose a SPORT application to run.
 */
final class AppChooser extends JFrame {
    // *************************************************************************
    // new methods exposed

    /**
     * Main entry point for the AppChooser application.
     *
     * @param arguments array of command-line arguments (not null)
     */
    public static void main(String[] arguments) {
        Logger.getLogger("").setLevel(Level.WARNING);
        List<BaseApplication> apps = new ArrayList<>(18);

        apps.add(new AssimpTest());
        apps.add(new CheckerboardTest());
        apps.add(new ClipspaceTest());
        apps.add(new ConveyorDemo());
        apps.add(new DynamicMeshTest());

        apps.add(new IcosphereTest());
        apps.add(new MouseTest());
        apps.add(new MouseTest2());
        apps.add(new NewtonsCradle());
        apps.add(new OctasphereTest());

        apps.add(new Pachinko());
        apps.add(new RainbowTest());
        apps.add(new SplitDemo());
        apps.add(new SpriteTest());
        apps.add(new TestGearJoint());

        apps.add(new TextureTest());
        apps.add(new ThousandCubes());
        apps.add(new Windlass());

        new AppChooser(apps);
    }
    // *************************************************************************
    // private methods

    /**
     * Select and run one SPORT app from the specified list.
     *
     * @param apps the list of apps to choose from (not null, unaffected)
     */
    private AppChooser(List<? extends BaseApplication> apps) {
        setTitle("SPORT AppChooser");
        setSize(500, 100);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        Container contentPane = getContentPane();

        // Add a ComboBox to select one app.
        JComboBox<String> comboBox = new JComboBox<>();
        for (BaseApplication app : apps) {
            String appName = app.getClass().getSimpleName();
            comboBox.addItem(appName);
        }
        contentPane.add(BorderLayout.CENTER, comboBox);

        // Add a JButton to start the selected app.
        JButton startButton = new JButton("Start the selected app");
        startButton.addActionListener(actionEvent -> {
            setVisible(false);
            int selectedIndex = comboBox.getSelectedIndex();
            apps.get(selectedIndex).start();
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        contentPane.add(BorderLayout.EAST, startButton);

        setVisible(true);
    }
}
