package me.mrgazdag.hibiscus.defaultplugin;

import me.mrgazdag.hibiscus.library.event.Event;
import me.mrgazdag.hibiscus.library.plugin.Plugin;
import me.mrgazdag.hibiscus.library.ui.UIManager;
import me.mrgazdag.hibiscus.library.ui.component.ButtonComponent;
import me.mrgazdag.hibiscus.library.ui.component.TextBoxComponent;
import me.mrgazdag.hibiscus.library.ui.component.TitleBoxComponent;
import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.page.PageGroup;
import me.mrgazdag.hibiscus.library.ui.page.PageIcons;
import me.mrgazdag.hibiscus.library.ui.property.filters.UserPropertyFilter;
import me.mrgazdag.hibiscus.library.users.ConnectedUser;

public class DefaultPlugin extends Plugin {
    @Override
    public void onEnable() {
        getLogger().println("Running server version: " + getLibraryServer().getAPIVersion());
        getLibraryServer().getEventManager().registerEventHandler(Event.class, this, e->{
            System.out.println(e);
        });

        UIManager ui = getLibraryServer().getUIManager();

        PageGroup group = new PageGroup(ui, "navigation");
        group.getGroupName().setDefaultValue("Navigation");
        group.register();

        Page home = new Page(ui, "home");
        home.getPageName().setDefaultValue("Home");
        home.getPageIcon().setDefaultValue(PageIcons.MATERIAL_HOME);
        home.setGroup(group);

        TitleBoxComponent title = home.createTitleBox();
        title.getTitleText().setDefaultValue("Welcome!");
        title.getTitleText().addUserFilter(user -> "Welcome, " + user.getEffectiveName() + "!");

        TextBoxComponent box = home.createTextBox();
        box.getText().setDefaultValue("This is a __default__ message that has **Markdown**.");
        box.getText().addUserFilter(new UserPropertyFilter<>() {
            @Override
            public String apply(ConnectedUser user) {
                return "This a message, that is specific to **" + user.getEffectiveName() + "**!";
            }

            @Override
            public boolean test(ConnectedUser user) {
                return !user.isGuest();
            }
        });

        ButtonComponent button = home.createButton();
        button.onPress().setHandler((device, v) -> {
            System.out.println(device.getDeviceId() + " pressed the button.");
        });

        home.register();

        Page customTest = new Page(ui, "customTest", "param");
        TextBoxComponent box2 = customTest.createTextBox();
        box2.getText().setDefaultValue("Default value lmao");
        box2.getText().addContextFilter(context -> String.valueOf(context.parameter("param")));
        customTest.setGroup(group);
        customTest.register();

        Page search = new Page(ui, "search");
        search.getPageName().setDefaultValue("Search");
        search.getPageIcon().setDefaultValue(PageIcons.MATERIAL_SEARCH);
        search.setGroup(group);
        search.register();

        PageGroup group2 = new PageGroup(ui, "othergroup");
        group2.getGroupName().setDefaultValue("Other Group");
        group2.register();

        Page playlists = new Page(ui, "playlists");
        playlists.getPageName().setDefaultValue("Playlists");
        playlists.getPageIcon().setDefaultValue(PageIcons.MATERIAL_LIBRARY_MUSIC);
        playlists.setGroup(group2);
        playlists.register();

        Page live = new Page(ui, "live");
        live.getPageName().setDefaultValue("Live");
        live.getPageIcon().setDefaultValue(PageIcons.MATERIAL_PODCASTS);
        live.setGroup(group2);
        live.register();

        ui.setDefaultPage(home);
    }
}
