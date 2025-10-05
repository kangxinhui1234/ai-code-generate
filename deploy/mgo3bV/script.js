// 产品数据
const products = [
    {
        id: 1,
        name: "波普艺术T恤",
        price: 299,
        category: "clothing",
        image: "https://picsum.photos/300/300?random=1"
    },
    {
        id: 2,
        name: "安迪·沃霍尔帆布包",
        price: 199,
        category: "accessories",
        image: "https://picsum.photos/300/300?random=2"
    },
    {
        id: 3,
        name: "波普风格手机壳",
        price: 89,
        category: "accessories",
        image: "https://picsum.photos/300/300?random=3"
    },
    {
        id: 4,
        name: "限量版波普海报",
        price: 159,
        category: "art",
        image: "https://picsum.photos/300/300?random=4"
    },
    {
        id: 5,
        name: "波普艺术卫衣",
        price: 399,
        category: "clothing",
        image: "https://picsum.photos/300/300?random=5"
    },
    {
        id: 6,
        name: "玛丽莲梦露装饰画",
        price: 259,
        category: "art",
        image: "https://picsum.photos/300/300?random=6"
    }
];

// 购物车数据
let cart = [];
let cartCount = 0;
let totalAmount = 0;

// DOM 加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    // 初始化产品展示
    displayProducts(products);
    
    // 初始化购物车
    updateCartDisplay();
    
    // 事件监听器
    setupEventListeners();
});

// 设置事件监听器
function setupEventListeners() {
    // 筛选按钮
    const filterButtons = document.querySelectorAll('.filter-btn');
    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            // 移除所有按钮的active类
            filterButtons.forEach(btn => btn.classList.remove('active'));
            // 给当前点击的按钮添加active类
            this.classList.add('active');
            
            // 根据筛选条件显示产品
            const filter = this.getAttribute('data-filter');
            filterProducts(filter);
        });
    });
    
    // 购物车按钮
    const cartBtn = document.querySelector('.cart-btn');
    const closeCart = document.querySelector('.close-cart');
    const cartSidebar = document.querySelector('.cart-sidebar');
    
    cartBtn.addEventListener('click', function() {
        cartSidebar.classList.add('active');
    });
    
    closeCart.addEventListener('click', function() {
        cartSidebar.classList.remove('active');
    });
    
    // CTA按钮
    const ctaButton = document.querySelector('.cta-button');
    ctaButton.addEventListener('click', function() {
        document.querySelector('#products').scrollIntoView({
            behavior: 'smooth'
        });
    });
    
    // 导航链接平滑滚动
    const navLinks = document.querySelectorAll('.nav-menu a');
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');
            document.querySelector(targetId).scrollIntoView({
                behavior: 'smooth'
            });
        });
    });
    
    // 联系表单提交
    const contactForm = document.querySelector('.contact-form');
    contactForm.addEventListener('submit', function(e) {
        e.preventDefault();
        alert('感谢您的留言！我们会尽快回复您。');
        this.reset();
    });
}

// 显示产品
function displayProducts(productsToShow) {
    const productsGrid = document.querySelector('.products-grid');
    productsGrid.innerHTML = '';
    
    productsToShow.forEach(product => {
        const productCard = document.createElement('div');
        productCard.className = 'product-card';
        productCard.innerHTML = `
            <div class="product-image">
                <img src="${product.image}" alt="${product.name}" style="width:100%;height:100%;object-fit:cover;">
            </div>
            <div class="product-info">
                <h3 class="product-title">${product.name}</h3>
                <p class="product-price">¥${product.price}</p>
                <button class="add-to-cart" data-id="${product.id}">加入购物车</button>
            </div>
        `;
        productsGrid.appendChild(productCard);
    });
    
    // 为所有"加入购物车"按钮添加事件监听器
    const addToCartButtons = document.querySelectorAll('.add-to-cart');
    addToCartButtons.forEach(button => {
        button.addEventListener('click', function() {
            const productId = parseInt(this.getAttribute('data-id'));
            addToCart(productId);
        });
    });
}

// 筛选产品
function filterProducts(category) {
    if (category === 'all') {
        displayProducts(products);
    } else {
        const filteredProducts = products.filter(product => product.category === category);
        displayProducts(filteredProducts);
    }
}

// 添加到购物车
function addToCart(productId) {
    const product = products.find(p => p.id === productId);
    const existingItem = cart.find(item => item.id === productId);
    
    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        cart.push({
            ...product,
            quantity: 1
        });
    }
    
    // 更新购物车显示
    updateCartDisplay();
    
    // 显示添加成功提示
    showNotification(`${product.name} 已添加到购物车`);
}

// 更新购物车显示
function updateCartDisplay() {
    // 更新购物车数量
    cartCount = cart.reduce((total, item) => total + item.quantity, 0);
    document.querySelector('.cart-count').textContent = cartCount;
    
    // 更新购物车总金额
    totalAmount = cart.reduce((total, item) => total + (item.price * item.quantity), 0);
    document.querySelector('.total-amount').textContent = `¥${totalAmount}`;
    
    // 更新购物车项目列表
    const cartItems = document.querySelector('.cart-items');
    cartItems.innerHTML = '';
    
    if (cart.length === 0) {
        cartItems.innerHTML = '<p>购物车为空</p>';
        return;
    }
    
    cart.forEach(item => {
        const cartItem = document.createElement('div');
        cartItem.className = 'cart-item';
        cartItem.innerHTML = `
            <div class="cart-item-image">
                <img src="${item.image}" alt="${item.name}" style="width:100%;height:100%;object-fit:cover;border-radius:10px;">
            </div>
            <div class="cart-item-details">
                <h4 class="cart-item-title">${item.name}</h4>
                <p class="cart-item-price">¥${item.price} × ${item.quantity}</p>
            </div>
            <button class="cart-item-remove" data-id="${item.id}">×</button>
        `;
        cartItems.appendChild(cartItem);
    });
    
    // 为所有"移除"按钮添加事件监听器
    const removeButtons = document.querySelectorAll('.cart-item-remove');
    removeButtons.forEach(button => {
        button.addEventListener('click', function() {
            const productId = parseInt(this.getAttribute('data-id'));
            removeFromCart(productId);
        });
    });
    
    // 结算按钮事件
    const checkoutBtn = document.querySelector('.checkout-btn');
    checkoutBtn.addEventListener('click', function() {
        if (cart.length === 0) {
            alert('购物车为空，无法结算');
            return;
        }
        
        alert(`订单提交成功！总金额：¥${totalAmount}`);
        cart = [];
        updateCartDisplay();
        document.querySelector('.cart-sidebar').classList.remove('active');
    });
}

// 从购物车移除商品
function removeFromCart(productId) {
    cart = cart.filter(item => item.id !== productId);
    updateCartDisplay();
}

// 显示通知
function showNotification(message) {
    // 创建通知元素
    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: var(--pop-green);
        color: white;
        padding: 15px 20px;
        border-radius: 5px;
        z-index: 1002;
        box-shadow: 0 3px 10px rgba(0,0,0,0.2);
        transition: transform 0.3s, opacity 0.3s;
    `;
    
    document.body.appendChild(notification);
    
    // 3秒后移除通知
    setTimeout(() => {
        notification.style.transform = 'translateX(100%)';
        notification.style.opacity = '0';
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}