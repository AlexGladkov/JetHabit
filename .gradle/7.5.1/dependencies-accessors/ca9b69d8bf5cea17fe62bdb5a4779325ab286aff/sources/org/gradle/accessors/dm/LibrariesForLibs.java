package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the `libs` extension.
*/
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final KviewmodelLibraryAccessors laccForKviewmodelLibraryAccessors = new KviewmodelLibraryAccessors(owner);
    private final LibresLibraryAccessors laccForLibresLibraryAccessors = new LibresLibraryAccessors(owner);
    private final OdysseyLibraryAccessors laccForOdysseyLibraryAccessors = new OdysseyLibraryAccessors(owner);
    private final SqldelightLibraryAccessors laccForSqldelightLibraryAccessors = new SqldelightLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(providers, config);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers) {
        super(config, providers);
    }

        /**
         * Creates a dependency provider for kodein (org.kodein.di:kodein-di)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getKodein() { return create("kodein"); }

    /**
     * Returns the group of libraries at kviewmodel
     */
    public KviewmodelLibraryAccessors getKviewmodel() { return laccForKviewmodelLibraryAccessors; }

    /**
     * Returns the group of libraries at libres
     */
    public LibresLibraryAccessors getLibres() { return laccForLibresLibraryAccessors; }

    /**
     * Returns the group of libraries at odyssey
     */
    public OdysseyLibraryAccessors getOdyssey() { return laccForOdysseyLibraryAccessors; }

    /**
     * Returns the group of libraries at sqldelight
     */
    public SqldelightLibraryAccessors getSqldelight() { return laccForSqldelightLibraryAccessors; }

    /**
     * Returns the group of versions at versions
     */
    public VersionAccessors getVersions() { return vaccForVersionAccessors; }

    /**
     * Returns the group of bundles at bundles
     */
    public BundleAccessors getBundles() { return baccForBundleAccessors; }

    /**
     * Returns the group of plugins at plugins
     */
    public PluginAccessors getPlugins() { return paccForPluginAccessors; }

    public static class KviewmodelLibraryAccessors extends SubDependencyFactory {

        public KviewmodelLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for compose (com.adeo:kviewmodel-compose)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCompose() { return create("kviewmodel.compose"); }

            /**
             * Creates a dependency provider for core (com.adeo:kviewmodel)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() { return create("kviewmodel.core"); }

            /**
             * Creates a dependency provider for odyssey (com.adeo:kviewmodel-odyssey)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getOdyssey() { return create("kviewmodel.odyssey"); }

    }

    public static class LibresLibraryAccessors extends SubDependencyFactory {

        public LibresLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for compose (io.github.skeptick.libres:libres-compose)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCompose() { return create("libres.compose"); }

    }

    public static class OdysseyLibraryAccessors extends SubDependencyFactory {

        public OdysseyLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for compose (io.github.alexgladkov:odyssey-compose)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCompose() { return create("odyssey.compose"); }

            /**
             * Creates a dependency provider for core (io.github.alexgladkov:odyssey-core)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() { return create("odyssey.core"); }

    }

    public static class SqldelightLibraryAccessors extends SubDependencyFactory {

        public SqldelightLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for android (app.cash.sqldelight:android-driver)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getAndroid() { return create("sqldelight.android"); }

            /**
             * Creates a dependency provider for desktop (app.cash.sqldelight:sqlite-driver)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getDesktop() { return create("sqldelight.desktop"); }

            /**
             * Creates a dependency provider for js (app.cash.sqldelight:sqljs-driver)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJs() { return create("sqldelight.js"); }

            /**
             * Creates a dependency provider for native (app.cash.sqldelight:native-driver)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getNative() { return create("sqldelight.native"); }

    }

    public static class VersionAccessors extends VersionFactory  {

        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: kodein (7.17.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getKodein() { return getVersion("kodein"); }

            /**
             * Returns the version associated to this alias: kviewmodel (0.13)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getKviewmodel() { return getVersion("kviewmodel"); }

            /**
             * Returns the version associated to this alias: libres (1.1.5)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLibres() { return getVersion("libres"); }

            /**
             * Returns the version associated to this alias: odyssey (1.3.70-native)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getOdyssey() { return getVersion("odyssey"); }

            /**
             * Returns the version associated to this alias: sqldelight (2.0.0-alpha05)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSqldelight() { return getVersion("sqldelight"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
