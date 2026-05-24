import fs from "fs";
import os from "os";
import path from "path";
import {spawnSync} from "child_process";
import {fileURLToPath} from "url";

//This file generates the GeneratedTFC BarrelMixinData

const generatorDir = path.dirname(fileURLToPath(import.meta.url));
const projectDir = path.resolve(generatorDir, "..");

const DEFAULT_OUT = path.join(
    projectDir,
    "src",
    "main",
    "java",
    "net",
    "chauvedev",
    "woodencog",
    "datagen",
    "recipe",
    "GeneratedTFCBarrelMixingData.java"
);

const args = parseArgs(process.argv.slice(2));
const tfcJar = path.resolve(projectDir, args.jar ?? process.env.TFC_JAR ?? findDefaultTfcJar());
const outFile = path.resolve(projectDir, args.out ?? DEFAULT_OUT);
const packageName = args.package ?? "net.chauvedev.woodencog.datagen.recipe";
const className = path.basename(outFile, ".java");

if (!fs.existsSync(tfcJar)) {
    throw new Error(`TFC jar not found: ${tfcJar}`);
}

const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), "woodencog-tfc-barrel-"));

try {
    extractBarrelRecipes(tfcJar, tempDir);

    const barrelDir = path.join(tempDir, "data", "tfc", "recipe", "barrel");
    const allRecipes = walkJsonFiles(barrelDir)
        .map(file => readBarrelRecipe(file, barrelDir))
        .filter(recipe => recipe !== null)
        .sort((a, b) => a.id.localeCompare(b.id));
    const recipes = allRecipes.filter(recipe => recipe.supportedForMixing);

    fs.mkdirSync(path.dirname(outFile), {recursive: true});
    fs.writeFileSync(outFile, renderJava(packageName, className, recipes), "utf8");

    console.log(`Generated ${recipes.length} barrel recipes in ${path.relative(projectDir, outFile)}`);
    console.log(`Skipped ${allRecipes.length - recipes.length} dynamic or unsupported barrel recipes.`);
} finally {
    fs.rmSync(tempDir, {recursive: true, force: true});
}

function parseArgs(argv) {
    const parsed = {};
    for (let i = 0; i < argv.length; i++) {
        const arg = argv[i];
        if (!arg.startsWith("--")) {
            continue;
        }

        const [key, inlineValue] = arg.slice(2).split("=", 2);
        parsed[key] = inlineValue ?? argv[++i];
    }
    return parsed;
}

function findDefaultTfcJar() {
    const libs = path.join(projectDir, "libs");
    if (!fs.existsSync(libs)) {
        return "";
    }

    const jars = fs.readdirSync(libs)
        .filter(file => /^TerraFirmaCraft.*\.jar$/i.test(file))
        .sort();

    return jars.length === 0 ? "" : path.join(libs, jars[jars.length - 1]);
}

function jarExecutable() {
    if (process.env.JAVA_HOME) {
        const exe = process.platform === "win32" ? "jar.exe" : "jar";
        const fromJavaHome = path.join(process.env.JAVA_HOME.replace(/[;]+$/, ""), "bin", exe);
        if (fs.existsSync(fromJavaHome)) {
            return fromJavaHome;
        }
    }
    return process.platform === "win32" ? "jar.exe" : "jar";
}

function extractBarrelRecipes(jarPath, destination) {
    const result = spawnSync(
        jarExecutable(),
        ["xf", jarPath, "data/tfc/recipe/barrel"],
        {cwd: destination, encoding: "utf8"}
    );

    if (result.status !== 0) {
        throw new Error(`Failed to extract barrel recipes from ${jarPath}\n${result.stderr}`);
    }
}

function walkJsonFiles(dir) {
    if (!fs.existsSync(dir)) {
        return [];
    }

    return fs.readdirSync(dir, {withFileTypes: true}).flatMap(entry => {
        const fullPath = path.join(dir, entry.name);
        if (entry.isDirectory()) {
            return walkJsonFiles(fullPath);
        }
        return entry.isFile() && entry.name.endsWith(".json") ? [fullPath] : [];
    });
}

function readBarrelRecipe(file, barrelDir) {
    const json = JSON.parse(fs.readFileSync(file, "utf8"));
    if (!["tfc:barrel_sealed", "tfc:barrel_instant", "tfc:barrel_instant_fluid"].includes(json.type)) {
        return null;
    }

    const relative = path.relative(barrelDir, file).replaceAll(path.sep, "/");
    const id = `barrel/${relative.slice(0, -".json".length)}`;
    const inputItem = parseItemIngredient(json.input_item);
    const inputFluid = parseFluidIngredient(json.input_fluid ?? json.primary_fluid);
    const addedFluid = parseFluidIngredient(json.added_fluid);
    const outputItem = parseItemResult(json.output_item);
    const outputFluid = parseFluidResult(json.output_fluid);
    const processingTime = json.type === "tfc:barrel_sealed"
        ? Math.max(1, Math.floor((json.duration ?? 1) / 10))
        : 1;

    const supportedForMixing = Boolean(
        inputItem !== undefined
        && inputFluid !== undefined
        && addedFluid !== undefined
        && outputItem !== undefined
        && outputFluid !== undefined
        && (outputItem !== null || outputFluid !== null)
    );

    return {
        id,
        sourcePath: `data/tfc/recipe/barrel/${relative}`,
        type: json.type,
        inputItem,
        inputFluid,
        addedFluid,
        outputItem,
        outputFluid,
        processingTime,
        supportedForMixing,
    };
}

function parseItemIngredient(value) {
    if (value === undefined) {
        return null;
    }

    const normalized = unwrapIngredient(value.ingredient ?? value);
    const count = value.count ?? normalized.count ?? value.ingredient?.count ?? 1;

    if (normalized.item) {
        return {tag: null, items: [normalized.item], count};
    }
    if (normalized.tag) {
        return {tag: normalized.tag, items: [], count};
    }
    if (Array.isArray(normalized.children)) {
        const children = normalized.children.map(child => parseItemIngredient({...child, count: child.count ?? 1}));
        if (children.some(child => child === undefined || child === null)) {
            return undefined;
        }
        const childTags = children.map(child => child.tag).filter(Boolean);
        const childItems = children.flatMap(child => child.items);
        if (childTags.length > 0 && childItems.length > 0) {
            return undefined;
        }
        if (childTags.length > 1) {
            return undefined;
        }
        return {tag: childTags[0] ?? null, items: childItems, count};
    }
    if (normalized.type === "tfc:heat") {
        return undefined;
    }
    return undefined;
}

function parseFluidIngredient(value) {
    if (value === undefined) {
        return null;
    }

    const ingredient = value.ingredient ?? value.fluid ?? value.id;
    const amount = value.amount ?? 1;

    if (typeof ingredient === "string") {
        return {tag: null, id: ingredient, amount};
    }
    if (ingredient?.tag) {
        return {tag: ingredient.tag, id: null, amount};
    }
    if (ingredient?.fluid) {
        return {tag: null, id: ingredient.fluid, amount};
    }
    return undefined;
}

function parseItemResult(value) {
    if (value === undefined) {
        return null;
    }

    const id = value.id ?? value.item;
    const count = value.count ?? 1;

    if (id) {
        return {id, count};
    }
    if (value.modifiers) {
        return undefined;
    }
    return undefined;
}

function parseFluidResult(value) {
    if (value === undefined) {
        return null;
    }

    const id = value.id ?? value.fluid;
    const amount = value.amount ?? 1;

    if (id) {
        return {id, amount};
    }
    return undefined;
}

function unwrapIngredient(value) {
    let current = value;
    while (current?.ingredient) {
        current = current.ingredient;
    }
    return current ?? {};
}

function renderJava(packageName, className, recipes) {
    return `package ${packageName};

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

/**
 * Generated by generators/generateTFCBarrelMixingData.js.
 * Source: ${slash(path.relative(projectDir, tfcJar))}
 */
public final class ${className} {
    private ${className}() {
    }

    public record ItemInput(TagKey<Item> tag, List<ItemLike> items, int count) {
    }

    public record ItemResult(ItemLike item, int count) {
    }

    public record FluidResult(Fluid fluid, int amount) {
    }

    public record BarrelRecipe(
            String id,
            String sourcePath,
            String type,
            ItemInput inputItem,
            SizedFluidIngredient inputFluid,
            SizedFluidIngredient addedFluid,
            ItemResult outputItem,
            FluidResult outputFluid,
            int processingTime
    ) {
    }

    public static final List<BarrelRecipe> RECIPES = List.of(
${recipes.map(renderRecipe).join(",\n")}
    );

    private static ResourceLocation resource(String id) {
        return ResourceLocation.parse(id);
    }

    private static ItemLike item(String id) {
        return BuiltInRegistries.ITEM.get(resource(id));
    }

    private static Fluid fluid(String id) {
        return BuiltInRegistries.FLUID.get(resource(id));
    }

    private static TagKey<Item> itemTag(String id) {
        return TagKey.create(Registries.ITEM, resource(id));
    }

    private static TagKey<Fluid> fluidTag(String id) {
        return TagKey.create(Registries.FLUID, resource(id));
    }
}
`;
}

function renderRecipe(recipe) {
    return `            new BarrelRecipe(${[
        javaString(recipe.id),
        javaString(recipe.sourcePath),
        javaString(recipe.type),
        renderItemInput(recipe.inputItem),
        renderFluidInput(recipe.inputFluid),
        renderFluidInput(recipe.addedFluid),
        renderItemResult(recipe.outputItem),
        renderFluidResult(recipe.outputFluid),
        String(recipe.processingTime),
    ].join(", ")})`;
}

function renderItemInput(value) {
    if (value === null) {
        return "null";
    }
    const tag = value.tag ? `itemTag(${javaString(value.tag)})` : "null";
    const items = `List.of(${value.items.map(item => `item(${javaString(item)})`).join(", ")})`;
    return `new ItemInput(${tag}, ${items}, ${value.count})`;
}

function renderFluidInput(value) {
    if (value === null) {
        return "null";
    }
    if (value.tag) {
        return `SizedFluidIngredient.of(fluidTag(${javaString(value.tag)}), ${value.amount})`;
    }
    return `SizedFluidIngredient.of(fluid(${javaString(value.id)}), ${value.amount})`;
}

function renderItemResult(value) {
    if (value === null) {
        return "null";
    }
    return `new ItemResult(item(${javaString(value.id)}), ${value.count})`;
}

function renderFluidResult(value) {
    if (value === null) {
        return "null";
    }
    return `new FluidResult(fluid(${javaString(value.id)}), ${value.amount})`;
}

function javaString(value) {
    if (value === null || value === undefined) {
        return "null";
    }
    return `"${String(value)
        .replaceAll("\\", "\\\\")
        .replaceAll("\"", "\\\"")
        .replaceAll("\r", "\\r")
        .replaceAll("\n", "\\n")}"`;
}

function slash(value) {
    return value.replaceAll(path.sep, "/");
}
