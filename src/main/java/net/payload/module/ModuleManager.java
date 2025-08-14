

/**
 * A class to represent a system that manages all the Modules.
 */
package net.payload.module;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.payload.Payload;
import net.payload.api.IAddon;
import net.payload.event.events.KeyDownEvent;
import net.payload.event.events.Render2DEvent;
import net.payload.event.listeners.KeyDownListener;
import net.payload.event.listeners.Render2DListener;
import net.payload.module.modules.client.AntiCheat;
import net.payload.module.modules.combat.*;
import net.payload.module.modules.exploit.*;
import net.payload.module.modules.misc.*;
import net.payload.module.modules.movement.*;
import net.payload.module.modules.render.*;
import net.payload.module.modules.world.*;
import net.payload.settings.Setting;
import net.payload.settings.SettingManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil.Key;
import net.payload.module.modules.client.*;

import static net.payload.PayloadClient.MC;

public class ModuleManager implements KeyDownListener, Render2DListener {
	public ArrayList<Module> modules = new ArrayList<Module>();
	private MinecraftClient mc = MinecraftClient.getInstance();

	// Modules
	public AirPlace airPlace = new AirPlace();
	public Aimbot aimbot = new Aimbot();
	public AntiAFK antiAFK = new AntiAFK();
	public AntiAim antiAim = new AntiAim();
	public AntiCheat antiCheat = new AntiCheat();
	public AntiCrawl antiCrawl = new AntiCrawl();
	public AntiHunger antihunger = new AntiHunger();
	public AntiKnockback antiknockback = new AntiKnockback();
	public AntiPotion antipotion = new AntiPotion();
	public AttributeSwap attributeSwap = new AttributeSwap();
	public AutoClicker autoClicker = new AutoClicker();
	public AutoEat autoeat = new AutoEat();
	public AutoFarm autofarm = new AutoFarm();
	public AutoFish autofish = new AutoFish();
	public AutoOminous autoOminous = new AutoOminous();
	public AutoRespawn autorespawn = new AutoRespawn();
	public AutoSign autosign = new AutoSign();
	public AutoTool autoTool = new AutoTool();
	public AutoTotem autoTotem = new AutoTotem();
	public AutoWalk autowalk = new AutoWalk();
	public BedAura bedAura = new BedAura();
	public BlockHighlight blockHighlight = new BlockHighlight();
	public BoatFly boatFly = new BoatFly();
	public Breadcrumbs breadcrumbs = new Breadcrumbs();
	public BreakDelay breakDelay = new BreakDelay();
	public BreakESP breakESP = new BreakESP();
	public CameraClip cameraClip = new CameraClip();
	public ChestESP chestesp = new ChestESP();
	public ChorusExploit chorusExploit = new ChorusExploit();
	public CityBoss cityBoss = new CityBoss();
	public ClickTP clickTP = new ClickTP();
	public ClientGUI clientGUI = new ClientGUI();
	public Criticals criticals = new Criticals();
	public CrystalAura crystalaura = new CrystalAura();
	public ElytraBoost elytraPlus = new ElytraBoost();
	public ElytraBounce elytraBounce = new ElytraBounce();
	public ElytraPacket elytraPacket = new ElytraPacket();
	public EntityControl entityControl = new EntityControl();
	public EntityESP entityesp = new EntityESP();
	public EntitySpeed entitySpeed = new EntitySpeed();
	public EXPThrower expthrower = new EXPThrower();
	public FakePlayer fakeplayer = new FakePlayer();
	public FireworkPlus fireworkPlus = new FireworkPlus();
	public Fly fly = new Fly();
	public FluxTimer fluxTimer = new FluxTimer();
	public Fov fov = new Fov();
	public WindowFPS focusfps = new WindowFPS();
	public Freecam freecam = new Freecam();
	public Freelook freelook = new Freelook();
	public Fullbright fullbright = new Fullbright();
	public HighJump higherjump = new HighJump();
	public HotbarRefill hotbarRefill = new HotbarRefill();
	public InstantRebreak instantRebreak = new InstantRebreak();
	public InteractTweaks interactTweaks = new InteractTweaks();
	public ItemTags itemTags = new ItemTags();
	public ItemViewModel itemViewModel = new ItemViewModel();
	public Jesus jesus = new Jesus();
	public KillAura killaura = new KillAura();
	public LitematicaPrinter litematicaPrinter = new LitematicaPrinter();
	public MiddleMouse middleMouse = new MiddleMouse();
	public MoveFix moveFix = new MoveFix();
	public MovieMode movieMode = new MovieMode();
	public Nametags nametags = new Nametags();
	public NewChunks newChunks = new NewChunks();
	public NoAttackDelay noAttackDelay = new NoAttackDelay();
	public NoBob noBob = new NoBob();
	public NoFall nofall = new NoFall();
	public NoGhostBlocks noGhostBlocks = new NoGhostBlocks();
	public NoJumpDelay nojumpdelay = new NoJumpDelay();
	public Nocom nocom = new Nocom();
	public NoRender norender = new NoRender();
	public NoSlowdown noslowdown = new NoSlowdown();
	public NoWaterCollision noWaterCollision = new NoWaterCollision();
	public Notifications chatModule = new Notifications();
	public Nuker nuker = new Nuker();
	public PacketCancel packetCancel = new PacketCancel();
	public PacketControl packetControl = new PacketControl();
	public PacketEat packetEat = new PacketEat();
	public PacketLog packetLog = new PacketLog();
	public PacketMine packetMine = new PacketMine();
	public PearlPhase pearlPhase = new PearlPhase();
	public PearlSpoof pearlSpoof = new PearlSpoof();
	public PlayerESP playeresp = new PlayerESP();
	public PlayerTrail playerTrail = new PlayerTrail();
	public PortalGod portalGod = new PortalGod();
	public Reach reach = new Reach();
	public Rotations rotations = new Rotations();
	public Safewalk safewalk = new Safewalk();
	public Scaffold scaffold = new Scaffold();
	public Search search = new Search();
	public SelfTrap selfTrap = new SelfTrap();
	public SelfTrapTest selfTrapTest = new SelfTrapTest();
	public ServerNuker serverNuker = new ServerNuker();
	public Sneak sneak = new Sneak();
	public SpawnerESP spawneresp = new SpawnerESP();
	public SpawnerScan activatedSpawnerDetector = new SpawnerScan();
	public Speed speed = new Speed();
	public Sprint sprint = new Sprint();
	public StashFinder stashFinder = new StashFinder();
	public Step step = new Step();
	public Suicide suicide = new Suicide();
	public Surround surround = new Surround();
	public SurroundTest surroundTest = new SurroundTest();
	public TileBreaker tilebreaker = new TileBreaker();
	public Timer timer = new Timer();
	public Tooltips tooltips = new Tooltips();
	public ToTheMoon toTheMoon = new ToTheMoon();
	public Tracer tracer = new Tracer();
	public Trajectory trajectory = new Trajectory();
	public TriggerBot triggerBot = new TriggerBot();
	public TunnelESP tunnelESP = new TunnelESP();
	public WorldTweaks worldTweaks = new WorldTweaks();
	public XCarry xCarry = new XCarry();
	public XRay xray = new XRay();
	public YawLock yawLock = new YawLock();
	public Zoom zoom = new Zoom();

	public ModuleManager(List<IAddon> addons) {
		try {
			// Attempts to find each field of type Module and add it to the module list.
			for (Field field : ModuleManager.class.getDeclaredFields()) {
				if (!Module.class.isAssignableFrom(field.getType()))
					continue;
				Module module = (Module) field.get(this);
				addModule(module);
			}

			// Gets each Addon and adds their modules to the client.
			addons.stream().filter(Objects::nonNull).forEach(addon -> {
				addon.modules().forEach(module -> {
					addModule(module);
				});
			});
		} catch (Exception e) {
		}

		// Registers all Module settings to the settings manager.
		for (Module module : modules) {
			for (Setting<?> setting : module.getSettings()) {
				SettingManager.registerSetting(setting);
			}
		}

		Payload.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		Payload.getInstance().eventManager.AddListener(Render2DListener.class, this);
	}

	public void addModule(Module module) {
		modules.add(module);
	}

	public void disableAll() {
		for (Module module : modules) {
			module.state.setValue(false);
		}
	}

	public Module getModuleByName(String string) {
		for (Module module : modules) {
			if (module.getName().equalsIgnoreCase(string)) {
				return module;
			}
		}
		return null;
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (mc.currentScreen == null) {
			for (Module module : modules) {
				Key binding = module.getBind().getValue();
				if (binding.getCode() == event.GetKey()) {
					module.toggle();
				}
			}
		}
	}

	@Override
	public void onRender(Render2DEvent event) {
		if (MC.player != null && MC.world != null) {
			if (!rotations.state.getValue()) {
				rotations.toggle();
			}

			if (!antiCheat.state.getValue()) {
				antiCheat.toggle();
			}

			if (!clientGUI.state.getValue()) {
				clientGUI.toggle();
			}
		}
	}
}
