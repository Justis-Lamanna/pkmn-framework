package com.github.lucbui;

import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.framework.RepointUtils;
import com.github.lucbui.gba.GBAFrameworkFactory;
import com.github.lucbui.gba.gfx.GBAColor;
import com.github.lucbui.structures.SampleStructure;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        PkmnFramework
                .init("C:\\Users\\laman\\IdeaProjects\\pkmnframework\\src\\main\\resources\\test.hex")
                .frameworkFactory(new GBAFrameworkFactory())
                .start();
        //PkmnFramework.write(0, GBAColor.from(10, 20, 30));
        SampleStructure ss = PkmnFramework.read(4, SampleStructure.class);
        PkmnFramework.write(4, ss, RepointUtils.identityRepointStrategy());
    }
}
