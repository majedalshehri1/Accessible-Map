// customMarker.js
import { Coffee, Hospital, ShoppingBag, Trees, Utensils } from "lucide-vue-next";
import { h } from "vue";
import { renderToString } from "vue/server-renderer";

const categories = {
    RESTAURANT: {
        icon: Utensils,
        color: "#f97316",
    },
    COFFEE: {
        icon: Coffee,
        color: "#451a03",
    },
    PARK: {
        icon: Trees,
        color: "#22c55e",
    },
    HOSPITAL: {
        icon: Hospital,
        color: "#ef4444",
    },
    MALL: {
        icon: ShoppingBag,
        color: "#0ea5e9",
    },
};

/**
 * generate marker HTML string for Leaflet
 * @param {string} category - category key (e.g., "COFFEE", "PARK")
 * @returns {Promise<string>} HTML string
 */
export async function getMarkerHtml(category) {
    const config = categories[category] || {};
    const IconComponent = config.icon;
    const color = config.color || "#000";

    let iconHtml = "";
    if (IconComponent) {
        // convert vue component (Lucide icon) to raw SVG string
        const vnode = h(IconComponent, { size: 16, color: "white", "stroke-width": 2 });
        iconHtml = await renderToString(vnode);
    }

    return `
    <div style="
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: ${color};
      border: 3px solid white;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
      box-shadow: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1);
    ">
      ${iconHtml}
    </div>
  `;
}
