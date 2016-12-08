imitations under the License.
 */

package com.fusionjack.slimota.fragments;
     addPreferencesFromResource(R.xml.slimota);

        mRomInfo = (PreferenceScreen) getPreferenceScreen().findPreference(KEY_ROM_INFO);
        mCheckUpdate = (PreferenceScreen) getPreferenceScreen().findPreference(KEY_CHECK_UPDATE);

        mUpdateInterval = (ListPreference) getPreferenceScreen().findPreference(KEY_UPDATE_INTERVAL);
        if (mUpdateInterval != null) {
            mUpdateInterval.setOnPreferenceChangeListener(this);
        }

        mLinksCategory = (PreferenceCategory) getPreferenceScreen().findPreference(CATEGORY_LINKS);
    }

    private void updatePreferences() {
        updateRomInfo();
        
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onProgressCancelled() {
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    @Override
    public void onConfigChange() {
        updateLinks(true);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        final String key = preference.getKey();
        switch (key) {
            case KEY_CHECK_UPDATE:
                mTask = CheckUpdateTask.getInstance(false);
                if (!mTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getActivity());
                }
                return true;
            default:
                OTALink link = LinkConfig.getInstance().findLink(key, getActivity());
                if (link != null) {
                    OTAUtils.launchUrl(link.getUrl(), getActivity());
                }
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if (preference == mUpdateInterval) {
            AppConfig.persistUpdateIntervalIndex(Integer.valueOf((String) value), getActivity());
            return true;
        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(AppConfig.getLatestVersionKey())) {
            updateRomInfo();
        }
        if (key.equals(AppConfig.getLastCheckKey())) {
            updateLastCheckSummary();
        }
        if (key.equals(AppConfig.getUpdateIntervalKey())) {
            updateIntervalSummary();
        }
    }
}
