package com.example.clockclone.framework.di.components;

import com.example.clockclone.framework.di.modules.MainModuleBinds;
import com.example.clockclone.framework.di.modules.MainModuleProvides;
import com.example.clockclone.framework.di.scopes.ApplicationScope;
import com.example.clockclone.ui.viewmodels.AddEditAlarmViewModel;
import com.example.clockclone.ui.viewmodels.AlarmsViewModel;
import com.example.clockclone.ui.viewmodels.StopwatchViewModel;
import com.example.clockclone.ui.viewmodels.WorldClockViewModel;

import dagger.Component;

@ApplicationScope
@Component(modules = {MainModuleProvides.class, MainModuleBinds.class})
public abstract class ApplicationComponent {
    public abstract void inject(WorldClockViewModel worldClockViewModel);
    public abstract void inject(AlarmsViewModel alarmsViewModel);
    public abstract void inject(StopwatchViewModel stopwatchViewModel);
    public abstract void inject(AddEditAlarmViewModel addEditAlarmViewModel);
}
