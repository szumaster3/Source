package content.global.skill.summoning.pet;

import content.global.skill.summoning.familiar.Familiar;
import content.global.skill.summoning.familiar.FamiliarSpecial;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.game.world.GameWorld;
import org.rs.consts.NPCs;

import static core.api.ContentAPIKt.setVarp;

/**
 * The type Pet.
 */
public final class Pet extends Familiar {

    private final int itemId;

    private final PetDetails details;

    private double growthRate;

    private final Pets pet;

    private int hasWarned = 0;

    /**
     * Instantiates a new Pet.
     *
     * @param owner   the owner
     * @param details the details
     * @param itemId  the item id
     * @param id      the id
     */
    public Pet(Player owner, final PetDetails details, int itemId, int id) {
        super(owner, id, -1, -1, -1);
        this.pet = Pets.forId(itemId);
        this.details = details;
        this.itemId = itemId;
        this.growthRate = pet.growthRate;
    }

    @Override
    public void sendConfiguration() {
        setVarp(owner, 1175, ((int) details.getGrowth() << 1) | ((int) details.getHunger() << 9));
        setVarp(owner, 1174, getId());
        setVarp(owner, 448, itemId);
    }

    @Override
    public void handleTickActions() {
        final PetDetails petDetails = details;
        double hunger = petDetails.getHunger();
        if (hunger >= 75.0 && hunger <= 90.0 && hasWarned < 1) {
            owner.sendMessage("<col=ff0000>Your pet is getting hungry.</col>");
            hasWarned = 1;
        } else if (hunger >= 90.0 && hasWarned < 2) {
            owner.getPacketDispatch().sendMessage("<col=ff0000>Your pet is starving, feed it before it runs off.</col>");
            hasWarned = 2;
        }
        if (hunger >= 100.0 && growthRate != 0 && pet.food.length != 0) {
            owner.getFamiliarManager().removeDetails(this.getItemIdHash());
            owner.getFamiliarManager().dismiss();
            owner.getFamiliarManager().setFamiliar(null);
            setVarp(owner, 1175, 0);
            owner.sendMessage("<col=ff0000>Your pet has run away.</col>");
            return;
        }
        double growth = petDetails.getGrowth();
        double growthrate = pet.growthRate;
        if (growthrate > 0.000) {
            if (GameWorld.getSettings().isDevMode()) {
                growthrate *= 100;
            }
            petDetails.updateGrowth(growthrate);
            if (growth == 100.0) {
                growNextStage();
            }
        }
        if ((!isInvisible() && owner.getLocation().getDistance(getLocation()) > 12) || (isInvisible() && ticks % 25 == 0)) {
            if (!call()) {
                setInvisible(true);
            }
        } else if (!getPulseManager().hasPulseRunning()) {
            startFollowing();
        }
        setVarp(owner, 1175, ((int) details.getGrowth() << 1) | ((int) details.getHunger() << 9));
    }

    /**
     * Grow next stage.
     */
    public void growNextStage() {
        if (pet == null) {
            return;
        }
        int newItemId = pet.getNextStageItemId(itemId);
        if (newItemId == -1) {
            return;
        }
        if (pet.isKitten(itemId)) {
            owner.incrementAttribute("/save:stats_manager:cats_raised");
        }
        owner.getFamiliarManager().removeDetails(this.getItemIdHash());
        owner.getFamiliarManager().dismiss();
        owner.getPacketDispatch().sendMessage("<col=ff0000>Your pet has grown larger.</col>");
        int npcId = pet.getNpcId(newItemId);
        details.updateGrowth(-100.0);
        Pet newPet = new Pet(owner, details, newItemId, npcId);
        newPet.growthRate = growthRate;
        newPet.hasWarned = hasWarned;
        owner.getFamiliarManager().setFamiliar(newPet);
        owner.getFamiliarManager().spawnFamiliar();
    }

    @Override
    public Familiar construct(Player owner, int id) {
        return this;
    }

    @Override
    protected boolean specialMove(FamiliarSpecial special) {
        return false;
    }

    @Override
    public boolean isCombatFamiliar() {
        return false;
    }

    /**
     * Gets item id.
     *
     * @return the item id
     */
    public int getItemId() {
        return itemId;
    }

    /**
     * Gets item id hash.
     *
     * @return the item id hash
     */
    public int getItemIdHash() {
        Item item = new Item(itemId);
        item.setCharge(details.getIndividual());
        return item.getIdHash();
    }

    /**
     * Gets details.
     *
     * @return the details
     */
    public PetDetails getDetails() {
        return details;
    }

    /**
     * Gets pet.
     *
     * @return the pet
     */
    public Pets getPet() {
        return pet;
    }


    @Override
    public int[] getIds() {
        return new int[]{
                NPCs.KITTEN_761,
                NPCs.KITTEN_762,
                NPCs.KITTEN_763,
                NPCs.KITTEN_764,
                NPCs.KITTEN_765,
                NPCs.KITTEN_766,
                NPCs.CAT_768,
                NPCs.CAT_769,
                NPCs.CAT_770,
                NPCs.CAT_771,
                NPCs.CAT_772,
                NPCs.CAT_773,
                NPCs.OVERGROWN_CAT_774,
                NPCs.OVERGROWN_CAT_775,
                NPCs.OVERGROWN_CAT_776,
                NPCs.OVERGROWN_CAT_777,
                NPCs.OVERGROWN_CAT_778,
                NPCs.OVERGROWN_CAT_779,
                NPCs.OVERGROWN_HELLCAT_3503,
                NPCs.HELLCAT_3504,
                NPCs.HELL_KITTEN_3505,
                NPCs.CLOCKWORK_CAT_3598,
                NPCs.HATCHLING_DRAGON_6900,
                NPCs.BABY_DRAGON_6901,
                NPCs.HATCHLING_DRAGON_6902,
                NPCs.BABY_DRAGON_6903,
                NPCs.HATCHLING_DRAGON_6904,
                NPCs.BABY_DRAGON_6905,
                NPCs.HATCHLING_DRAGON_6906,
                NPCs.BABY_DRAGON_6907,
                NPCs.BABY_PENGUIN_6908,
                NPCs.PENGUIN_6909,
                NPCs.RAVEN_CHICK_6911,
                NPCs.RAVEN_6912,
                NPCs.BABY_RACCOON_6913,
                NPCs.RACCOON_6914,
                NPCs.BABY_GECKO_6915,
                NPCs.GECKO_6916,
                NPCs.BABY_SQUIRREL_6919,
                NPCs.SQUIRREL_6920,
                NPCs.BABY_CHAMELEON_6922,
                NPCs.CHAMELEON_6923,
                NPCs.BABY_MONKEY_6942,
                NPCs.MONKEY_6943,
                NPCs.VULTURE_CHICK_6945,
                NPCs.VULTURE_6946,
                NPCs.BABY_GIANT_CRAB_6947,
                NPCs.GIANT_CRAB_6948,
                NPCs.SARADOMIN_CHICK_6949,
                NPCs.SARADOMIN_BIRD_6950,
                NPCs.SARADOMIN_OWL_6951,
                NPCs.ZAMORAK_CHICK_6952,
                NPCs.ZAMORAK_BIRD_6953,
                NPCs.ZAMORAK_HAWK_6954,
                NPCs.GUTHIX_CHICK_6955,
                NPCs.GUTHIX_BIRD_6956,
                NPCs.GUTHIX_RAPTOR_6957,
                NPCs.TERRIER_PUPPY_6958,
                NPCs.GREYHOUND_PUPPY_6960,
                NPCs.GREYHOUND_6961,
                NPCs.LABRADOR_PUPPY_6962,
                NPCs.LABRADOR_6963,
                NPCs.DALMATIAN_PUPPY_6964,
                NPCs.DALMATIAN_6965,
                NPCs.SHEEPDOG_PUPPY_6966,
                NPCs.SHEEPDOG_6967,
                NPCs.BULLDOG_6968,
                NPCs.BULLDOG_PUPPY_6969,
                NPCs.PLATYPUS_7015,
                NPCs.PLATYPUS_7016,
                NPCs.PLATYPUS_7017,
                NPCs.BABY_PLATYPUS_7018,
                NPCs.BABY_PLATYPUS_7019,
                NPCs.BABY_PLATYPUS_7020,
                NPCs.BABY_MONKEY_7210,
                NPCs.MONKEY_7211,
                NPCs.BABY_MONKEY_7212,
                NPCs.MONKEY_7213,
                NPCs.BABY_MONKEY_7214,
                NPCs.MONKEY_7215,
                NPCs.BABY_MONKEY_7216,
                NPCs.MONKEY_7217,
                NPCs.BABY_MONKEY_7218,
                NPCs.MONKEY_7219,
                NPCs.BABY_MONKEY_7220,
                NPCs.MONKEY_7221,
                NPCs.BABY_MONKEY_7222,
                NPCs.MONKEY_7223,
                NPCs.BABY_MONKEY_7224,
                NPCs.MONKEY_7225,
                NPCs.BABY_MONKEY_7226,
                NPCs.MONKEY_7227,
                NPCs.TERRIER_PUPPY_7237,
                NPCs.TERRIER_7238,
                NPCs.TERRIER_PUPPY_7239,
                NPCs.TERRIER_7240,
                NPCs.GREYHOUND_PUPPY_7241,
                NPCs.GREYHOUND_7242,
                NPCs.GREYHOUND_PUPPY_7243,
                NPCs.GREYHOUND_7244,
                NPCs.LABRADOR_PUPPY_7245,
                NPCs.LABRADOR_7246,
                NPCs.LABRADOR_PUPPY_7247,
                NPCs.LABRADOR_7248,
                NPCs.DALMATIAN_PUPPY_7249,
                NPCs.DALMATIAN_7250,
                NPCs.DALMATIAN_PUPPY_7251,
                NPCs.DALMATIAN_7252,
                NPCs.SHEEPDOG_PUPPY_7253,
                NPCs.SHEEPDOG_7254,
                NPCs.SHEEPDOG_PUPPY_7255,
                NPCs.SHEEPDOG_7256,
                NPCs.BULLDOG_7257,
                NPCs.BULLDOG_7258,
                NPCs.BULLDOG_PUPPY_7259,
                NPCs.BULLDOG_PUPPY_7260,
                NPCs.RAVEN_CHICK_7261,
                NPCs.RAVEN_7262,
                NPCs.RAVEN_CHICK_7263,
                NPCs.RAVEN_7264,
                NPCs.RAVEN_CHICK_7265,
                NPCs.RAVEN_7266,
                NPCs.RAVEN_CHICK_7267,
                NPCs.RAVEN_7268,
                NPCs.RAVEN_CHICK_7269,
                NPCs.RAVEN_7270,
                NPCs.BABY_RACCOON_7271,
                NPCs.RACCOON_7272,
                NPCs.BABY_RACCOON_7273,
                NPCs.RACCOON_7274,
                NPCs.BABY_GECKO_7277,
                NPCs.BABY_GECKO_7278,
                NPCs.BABY_GECKO_7279,
                NPCs.BABY_GECKO_7280,
                NPCs.GECKO_7281,
                NPCs.GECKO_7282,
                NPCs.GECKO_7283,
                NPCs.GECKO_7284,
                NPCs.BABY_GIANT_CRAB_7293,
                NPCs.GIANT_CRAB_7294,
                NPCs.BABY_GIANT_CRAB_7295,
                NPCs.GIANT_CRAB_7296,
                NPCs.BABY_GIANT_CRAB_7297,
                NPCs.GIANT_CRAB_7298,
                NPCs.BABY_GIANT_CRAB_7299,
                NPCs.GIANT_CRAB_7300,
                NPCs.BABY_SQUIRREL_7301,
                NPCs.SQUIRREL_7302,
                NPCs.BABY_SQUIRREL_7303,
                NPCs.SQUIRREL_7304,
                NPCs.BABY_SQUIRREL_7305,
                NPCs.SQUIRREL_7306,
                NPCs.BABY_SQUIRREL_7307,
                NPCs.SQUIRREL_7308,
                NPCs.BABY_PENGUIN_7313,
                NPCs.PENGUIN_7314,
                NPCs.BABY_PENGUIN_7316,
                NPCs.PENGUIN_7317,
                NPCs.VULTURE_CHICK_7319,
                NPCs.VULTURE_7320,
                NPCs.VULTURE_CHICK_7321,
                NPCs.VULTURE_7322,
                NPCs.VULTURE_CHICK_7323,
                NPCs.VULTURE_7324,
                NPCs.VULTURE_CHICK_7325,
                NPCs.VULTURE_7326,
                NPCs.VULTURE_CHICK_7327,
                NPCs.VULTURE_7328};
    }

}
