/**
 * Simple Algorithms Visualizer
 * Interactive visualization for sorting algorithms and adaptive search
 */

// Main Alpine.js component
document.addEventListener('alpine:init', () => {
  Alpine.data('algorithmVisualizer', () => ({
    // Connection state
    isConnected: false,
    serverInfo: '',
    messages: [],

    // Navigation
    activeTab: 'sorting',

    // Debug mode
    DEBUG: true,

    // Theme management
    currentTheme: localStorage.getItem('theme') || 'auto',

    // Sorting algorithm state
    sortingState: {
      arrayCreated: false,
      isRunning: false,
      isPaused: false,
      isCompleted: false,
      currentStep: 0,
      currentArray: []
    },

    // Sorting configuration
    sortingConfig: {
      dataType: 'random',
      size: 15,
      customData: '',
      listType: 'array',
      algorithm: 'bubble',
      speed: 500
    },

    // Sorting metrics
    sortingMetrics: {
      comparisons: 0,
      swaps: 0
    },

    // Search state
    searchState: {
      bagCreated: false,
      isRunning: false,
      lastSearchResult: 'None',
      simulationProgress: '0/0',
      currentStructure: [],
      currentSearchIndex: -1,
      foundIndex: -1,
      searchedIndices: []
    },

    // Search configuration
    searchConfig: {
      dataType: 'sequential',
      size: 10,
      customData: '',
      bagType: 'array',
      searchElement: null
    },

    // Search metrics
    searchMetrics: {
      comparisons: 0,
      modifications: 0
    },

    // Visualization instances
    sortingVisualization: null,
    searchVisualization: null,

    socketWrapper: null,

    init() {
      console.log('Simple Algorithms Visualizer initialized');
      this.initializeTheme();
      this.setupWebSocketHandlers();
      this.initializeVisualizations();
    },

    // WebSocket message handlers
    setupWebSocketHandlers() {
      // Listen for HTMX WebSocket events
      document.body.addEventListener('htmx:wsAfterMessage', (e) => {
        try {
          const message = JSON.parse(e.detail.message);
          this.handleWebSocketMessage(message);
        } catch (error) {
          console.error('Error parsing WebSocket message:', error);
        }
      });

      document.body.addEventListener('htmx:wsOpen', (e) => {
        this.isConnected = true;
        this.socketWrapper = e.detail.socketWrapper;
        console.log('WebSocket connected');
      });

      document.body.addEventListener('htmx:wsClose', (e) => {
        this.isConnected = false;
        console.log('WebSocket disconnected');
      });

      document.body.addEventListener('htmx:wsError', (e) => {
        this.isConnected = false;
        console.error('WebSocket error:', e.detail);
      });
    },

    handleWebSocketMessage(message) {
      console.log('Received message:', message);
      this.addMessage(message.type, message.message);

      switch (message.type) {
        case 'connected':
          this.handleConnected(message);
          break;
        case 'array_created':
          this.handleArrayCreated(message);
          break;
        case 'sort_started':
          this.handleSortStarted(message);
          break;
        case 'sorting_step':
          this.handleSortingStep(message);
          break;
        case 'sort_completed':
          this.handleSortCompleted(message);
          break;
        case 'sort_paused':
        case 'sort_resumed':
        case 'sort_reset':
          this.handleSortStateChange(message);
          break;
        case 'bag_created':
          this.handleBagCreated(message);
          break;
        case 'search_completed':
          this.handleSearchCompleted(message);
          break;
        case 'simulation_progress':
          this.handleSimulationProgress(message);
          break;
        case 'simulation_completed':
          this.handleSimulationCompleted(message);
          break;
        case 'error':
          this.handleError(message);
          break;
        default:
          console.warn('Unknown message type:', message.type);
      }
    },

    handleConnected(message) {
      this.serverInfo = message.data?.serverInfo || 'Connected';
      console.log('Connected to server:', message.data);
    },

    handleArrayCreated(message) {
      this.sortingState.arrayCreated = true;
      this.sortingState.currentArray = message.data.data || [];
      this.updateSortingVisualization();
      this.addMessage('success', 'Array created successfully!');
    },

    handleSortStarted(message) {
      this.sortingState.isRunning = true;
      this.sortingState.isPaused = false;
      this.sortingState.isCompleted = false;
      this.sortingState.currentStep = 0;
      this.resetSortingMetrics();
    },

    handleSortingStep(message) {
      const data = message.data;
      this.sortingState.currentStep = data.stepIndex || 0;
      this.sortingMetrics.comparisons = data.comparisons || 0;
      this.sortingMetrics.swaps = data.swaps || 0;

      if (data.arrayState) {
        this.sortingState.currentArray = data.arrayState;
        this.updateSortingVisualization(data.operation, data.index1, data.index2);
      }
    },

    handleSortCompleted(message) {
      this.sortingState.isRunning = false;
      this.sortingState.isCompleted = true;
      this.sortingMetrics.comparisons = message.data.comparisons || 0;
      this.sortingMetrics.swaps = message.data.swaps || 0;

      if (message.data.finalArray) {
        this.sortingState.currentArray = message.data.finalArray;
        this.updateSortingVisualization();
      }
    },

    handleSortStateChange(message) {
      switch (message.type) {
        case 'sort_paused':
          this.sortingState.isPaused = true;
          break;
        case 'sort_resumed':
          this.sortingState.isPaused = false;
          break;
        case 'sort_reset':
          this.resetSortingState();
          break;
      }
    },

    handleBagCreated(message) {
      this.searchState.bagCreated = true;
      this.searchState.currentStructure = message.data.data || [];
      this.resetSearchState();
      this.updateSearchVisualization();
    },

    handleSearchCompleted(message) {
      const data = message.data;
      this.searchState.isRunning = false;
      this.searchState.lastSearchResult = data.found ? `Found ${data.element}` : `Not found ${data.element}`;

      if (data.steps && data.steps.length > 0) {
        this.playSearchAnimation(data.steps);
      } else {
        // Fallback for no steps
        this.searchMetrics.comparisons = data.comparisons || 0;
        this.searchMetrics.modifications = data.modifications || 0;
        if (data.finalStructure) {
          this.searchState.currentStructure = data.finalStructure;
          this.updateSearchVisualization();
        }
      }
    },

    handleSimulationProgress(message) {
      const data = message.data;
      this.searchState.simulationProgress = `${data.iteration}/${data.total}`;

      if (data.currentStructure) {
        this.searchState.currentStructure = data.currentStructure;
        this.updateSearchVisualization();
      }
    },

    handleSimulationCompleted(message) {
      const data = message.data;
      this.searchState.simulationProgress = `${data.totalIterations}/${data.totalIterations} (Complete)`;
      this.searchState.lastSearchResult = `Simulation completed: ${data.pattern}`;

      if (data.finalStructure) {
        this.searchState.currentStructure = data.finalStructure;
        this.updateSearchVisualization();
      }
    },

    handleError(message) {
      console.error('Server error:', message.message);
    },

    // Message Management
    addMessage(type, message) {
      if (!message) return;
      const messageId = `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
      this.messages.unshift({
        id: messageId,
        type: type,
        message: message,
        timestamp: Date.now(),
      });
      if (this.messages.length > 100) {
        this.messages.pop();
      }
    },

    clearMessages() {
      this.messages = [];
    },

    formatTime(timestamp) {
      return new Date(timestamp).toLocaleTimeString();
    },

    // Navigation
    switchTab(tab) {
      this.activeTab = tab;
      // Reinitialize visualizations when switching tabs
      this.$nextTick(() => {
        if (tab === 'sorting') {
          this.initializeSortingVisualization();
        } else if (tab === 'search') {
          this.initializeSearchVisualization();
        }
      });
    },

    // Sorting methods
    updateSortingConfig() {
      // Configuration updated - array will need to be recreated
      this.sortingState.arrayCreated = false;
      this.resetSortingState();
    },

    createArray() {
      const config = this.sortingConfig;
      let data;

      if (config.dataType === 'custom') {
        if (!config.customData.trim()) {
          alert('Please enter custom data');
          return;
        }
        data = config.customData.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
        if (data.length === 0) {
          alert('Please enter valid numbers separated by commas');
          return;
        }
      } else {
        data = config.size;
      }

      this.sendMessage({
        type: 'create_array',
        data: {
          listType: config.listType,
          dataType: config.dataType,
          size: config.size,
          data: config.dataType === 'custom' ? data : undefined
        }
      });
    },

    startSort() {
      if (!this.sortingState.arrayCreated) {
        alert('Please create an array first');
        return;
      }

      this.sendMessage({
        type: 'start_sort',
        data: {
          algorithm: this.sortingConfig.algorithm
        }
      });
    },

    pauseSort() {
      this.sendMessage({ type: 'pause_sort', data: {} });
    },

    resumeSort() {
      this.sendMessage({ type: 'resume_sort', data: {} });
    },

    stepSort() {
      this.sendMessage({ type: 'step_sort', data: {} });
    },

    resetSort() {
      this.sendMessage({ type: 'reset_sort', data: {} });
      this.resetSortingState();
    },

    skipToEnd() {
      this.sendMessage({ type: 'skip_to_end', data: {} });
    },

    setAnimationSpeed() {
      this.sendMessage({
        type: 'set_speed',
        data: { speed: this.sortingConfig.speed }
      });
    },

    resetSortingState() {
      this.sortingState.isRunning = false;
      this.sortingState.isPaused = false;
      this.sortingState.isCompleted = false;
      this.sortingState.currentStep = 0;
      this.resetSortingMetrics();
    },

    resetSortingMetrics() {
      this.sortingMetrics.comparisons = 0;
      this.sortingMetrics.swaps = 0;
    },

    // Search methods
    updateSearchConfig() {
      this.searchState.bagCreated = false;
      this.resetSearchState();
    },

    createBag() {
      const config = this.searchConfig;
      let data;

      if (config.dataType === 'custom') {
        if (!config.customData.trim()) {
          alert('Please enter custom data');
          return;
        }
        data = config.customData.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
        if (data.length === 0) {
          alert('Please enter valid numbers separated by commas');
          return;
        }
      } else {
        data = config.size;
      }

      this.sendMessage({
        type: 'create_bag',
        data: {
          bagType: config.bagType,
          dataType: config.dataType,
          size: config.size,
          data: config.dataType === 'custom' ? data : undefined
        }
      });
    },

    searchElement() {
      if (!this.searchState.bagCreated) {
        alert('Please create a bag first');
        return;
      }

      if (!this.searchConfig.searchElement) {
        alert('Please enter an element to search');
        return;
      }

      this.sendMessage({
        type: 'search_element',
        data: {
          element: parseInt(this.searchConfig.searchElement)
        }
      });
    },

    runSimulation(pattern) {
      if (!this.searchState.bagCreated) {
        alert('Please create a bag first');
        return;
      }

      this.sendMessage({
        type: 'run_simulation',
        data: {
          pattern: pattern,
          iterations: 50
        }
      });
    },

    resetBag() {
      this.sendMessage({ type: 'reset_bag', data: {} });
      this.resetSearchState();
    },

    resetSearchState() {
      this.searchState.isRunning = false;
      this.searchState.lastSearchResult = 'None';
      this.searchState.simulationProgress = '0/0';
      this.searchState.currentSearchIndex = -1;
      this.searchState.foundIndex = -1;
      this.searchState.searchedIndices = [];
      this.resetSearchMetrics();
    },

    resetSearchMetrics() {
      this.searchMetrics.comparisons = 0;
      this.searchMetrics.modifications = 0;
    },

    // Visualization methods
    initializeVisualizations() {
      this.$nextTick(() => {
        this.initializeSortingVisualization();
        this.initializeSearchVisualization();
      });
    },

    initializeSortingVisualization() {
      const container = document.getElementById('sorting-visualization');
      if (container) {
        this.sortingVisualization = new SortingVisualization(container);
      }
    },

    initializeSearchVisualization() {
      const container = document.getElementById('search-visualization');
      if (container) {
        this.searchVisualization = new SearchVisualization(container);
      }
    },

    updateSortingVisualization(operation, index1, index2) {
      if (this.sortingVisualization) {
        this.sortingVisualization.updateArray(this.sortingState.currentArray, operation, index1, index2);
      }
    },

    updateSearchVisualization() {
      if (this.searchVisualization) {
        this.searchVisualization.updateStructure(
          this.searchState.currentStructure,
          this.searchConfig.bagType,
          {
            currentSearchIndex: this.searchState.currentSearchIndex,
            foundIndex: this.searchState.foundIndex,
            searchedIndices: this.searchState.searchedIndices || [],
          }
        );
      }
    },

    playSearchAnimation(steps) {
      this.searchState.isRunning = true;
      let stepIndex = 0;

      const animate = () => {
        if (stepIndex >= steps.length) {
          this.searchState.isRunning = false;
          // Final update to clear highlights
          const lastStep = steps[steps.length - 1];
          this.searchState.currentStructure = lastStep.structureState;
          this.searchMetrics.comparisons = lastStep.comparisons;
          this.searchMetrics.modifications = lastStep.modifications;
          this.searchState.currentSearchIndex = -1;
          this.searchState.foundIndex = lastStep.operation.includes("complete") ? lastStep.index : -1;
          this.updateSearchVisualization();
          return;
        }

        const step = steps[stepIndex];
        this.searchState.currentStructure = step.structureState;
        this.searchMetrics.comparisons = step.comparisons;
        this.searchMetrics.modifications = step.modifications;
        this.searchState.currentSearchIndex = -1;
        this.searchState.foundIndex = -1;

        if (step.operation === 'compare') {
          this.searchState.currentSearchIndex = step.index;
        } else if (step.operation.includes("complete")) {
          this.searchState.foundIndex = step.index;
        }

        this.updateSearchVisualization();
        stepIndex++;
        setTimeout(animate, 300); // 300ms delay between steps
      };

      animate();
    },

    // ===================================================================
    // DEBUG & THEME MANAGEMENT
    // ===================================================================

    toggleDebug() {
      this.DEBUG = !this.DEBUG;
      this.addMessage("info", `Debug mode ${this.DEBUG ? "enabled" : "disabled"}`);
    },

    debug(...args) {
      if (this.DEBUG) {
        console.log("[Simple Algorithms Visualizer]", ...args);
      }
    },

    // Theme Management
    initializeTheme() {
      this.applyTheme(this.currentTheme);
    },

    applyTheme(theme) {
      const root = document.documentElement;

      // Remove existing theme attributes
      root.removeAttribute('data-theme');

      if (theme === 'light') {
        root.setAttribute('data-theme', 'light');
      } else if (theme === 'dark') {
        root.setAttribute('data-theme', 'dark');
      }
      // For 'auto', we rely on the CSS @media (prefers-color-scheme: dark) rule

      this.currentTheme = theme;
      localStorage.setItem('theme', theme);

      this.debug('Theme applied:', theme);
    },

    toggleTheme() {
      const themes = ['light', 'dark', 'auto'];
      const currentIndex = themes.indexOf(this.currentTheme);
      const nextTheme = themes[(currentIndex + 1) % themes.length];

      this.applyTheme(nextTheme);
      this.addMessage("info", `Theme switched to: ${nextTheme}`);
    },

    getThemeIcon() {
      switch (this.currentTheme) {
        case 'light': return '☀️';
        case 'dark': return '🌙';
        case 'auto': return '🌓';
        default: return '🌓';
      }
    },

    getThemeLabel() {
      switch (this.currentTheme) {
        case 'light': return 'Light';
        case 'dark': return 'Dark';
        case 'auto': return 'Auto';
        default: return 'Auto';
      }
    },

    // Utility methods
    sendMessage(message) {
      console.log('Sending message:', message);

      if (!this.isConnected || !this.socketWrapper) {
        console.error('WebSocket not connected or ready');
        return;
      }

      this.socketWrapper.send(JSON.stringify(message));
    }
  }));
});

/**
 * Sorting Visualization Class
 */
class SortingVisualization {
  constructor(container) {
    this.container = container;
    this.setup();
  }

  setup() {
    this.container.innerHTML = '';
    this.container.style.display = 'flex';
    this.container.style.alignItems = 'flex-end';
    this.container.style.justifyContent = 'center';
    this.container.style.height = '300px';
    this.container.style.padding = '20px';
    this.container.style.backgroundColor = 'var(--theme-surface-elevated)';
    this.container.style.borderRadius = 'var(--radius-base)';
    this.container.style.border = '1px solid var(--theme-border)';
    this.container.style.gap = '2px';
  }

  updateArray(array, operation, index1, index2) {
    this.container.innerHTML = '';

    if (!array || array.length === 0) {
      this.container.innerHTML = '<p style="color: var(--text-muted);">No array to display</p>';
      return;
    }

    const maxValue = Math.max(...array);
    const barWidth = Math.min(50, (this.container.offsetWidth - 40) / array.length);

    array.forEach((value, index) => {
      const bar = document.createElement('div');
      bar.className = 'array-bar';
      bar.style.width = `${barWidth - 2}px`;
      bar.style.height = `${(value / maxValue) * 250}px`;
      bar.style.minHeight = '20px';
      bar.style.backgroundColor = this.getBarColor(operation, index, index1, index2);
      bar.style.border = '1px solid var(--theme-border-strong)';
      bar.style.display = 'flex';
      bar.style.alignItems = 'flex-end';
      bar.style.justifyContent = 'center';
      bar.style.transition = 'all var(--transition-fast)';
      bar.style.borderRadius = 'var(--radius-sm) var(--radius-sm) 0 0';
      bar.style.position = 'relative';

      // Add value label
      const label = document.createElement('span');
      label.textContent = value;
      label.style.fontSize = 'var(--font-size-xs)';
      label.style.color = 'var(--theme-text-primary)';
      label.style.fontWeight = 'var(--font-weight-semibold)';
      label.style.marginBottom = 'var(--space-1)';
      label.style.textShadow = '0 1px 2px rgba(0, 0, 0, 0.1)';
      bar.appendChild(label);

      this.container.appendChild(bar);
    });
  }

  getBarColor(operation, index, index1, index2) {
    if (operation === 'compare' && (index === index1 || index === index2)) {
      return '#ff4444'; // Bright red for comparisons - highly visible
    } else if (operation === 'swap' && (index === index1 || index === index2)) {
      return 'var(--color-warning)'; // Orange for swaps
    } else if (operation === 'swapped' && (index === index1 || index === index2)) {
      return 'var(--color-success)'; // Green for completed swaps
    }
    return '#3b82f6'; // Default blue color for normal bars
  }
}

/**
 * Search Visualization Class - Using Bar Visualization with Search Effects
 */
class SearchVisualization {
  constructor(container) {
    this.container = container;
    this.setup();
  }

  setup() {
    this.container.innerHTML = '';
    this.container.style.display = 'flex';
    this.container.style.alignItems = 'flex-end';
    this.container.style.justifyContent = 'center';
    this.container.style.height = '300px';
    this.container.style.padding = '20px';
    this.container.style.backgroundColor = 'var(--theme-surface-elevated)';
    this.container.style.borderRadius = 'var(--radius-base)';
    this.container.style.border = '1px solid var(--theme-border)';
    this.container.style.gap = '2px';
  }

  updateStructure(structure, bagType, searchState = {}) {
    this.container.innerHTML = '';

    if (!structure || structure.length === 0) {
      this.container.innerHTML = '<p style="color: var(--theme-text-muted);">No bag structure to display</p>';
      return;
    }

    const maxValue = Math.max(...structure);
    const barWidth = Math.min(50, (this.container.offsetWidth - 40) / structure.length);

    structure.forEach((value, index) => {
      const bar = this.createBagBar(value, index, barWidth, maxValue, bagType, searchState);
      this.container.appendChild(bar);
    });
  }

  createBagBar(value, index, barWidth, maxValue, bagType, searchState) {
    const bar = document.createElement('div');
    bar.className = 'search-bar';
    bar.style.width = `${barWidth - 2}px`;
    bar.style.height = `${(value / maxValue) * 250}px`;
    bar.style.minHeight = '30px';
    bar.style.backgroundColor = this.getBarColor(index, bagType, searchState);
    bar.style.border = '2px solid var(--theme-border-strong)';
    bar.style.display = 'flex';
    bar.style.flexDirection = 'column';
    bar.style.alignItems = 'center';
    bar.style.justifyContent = 'space-between';
    bar.style.padding = 'var(--space-1)';
    bar.style.transition = 'all var(--transition-base)';
    bar.style.borderRadius = 'var(--radius-sm) var(--radius-sm) 0 0';
    bar.style.position = 'relative';

    // Add special effects for search states
    if (searchState.currentSearchIndex === index) {
      bar.style.boxShadow = '0 0 15px var(--color-warning)';
      bar.style.transform = 'scale(1.05)';
      bar.style.zIndex = '10';
    }

    // Add value label at bottom
    const valueLabel = document.createElement('span');
    valueLabel.textContent = value;
    valueLabel.style.fontSize = 'var(--font-size-sm)';
    valueLabel.style.color = 'var(--theme-text-primary)';
    valueLabel.style.fontWeight = 'var(--font-weight-bold)';
    valueLabel.style.textShadow = '0 1px 2px rgba(0, 0, 0, 0.3)';
    bar.appendChild(valueLabel);

    // Add index label at top
    const indexLabel = document.createElement('span');
    indexLabel.textContent = index;
    indexLabel.style.fontSize = 'var(--font-size-xs)';
    indexLabel.style.color = 'var(--theme-text-secondary)';
    indexLabel.style.opacity = '0.8';
    bar.appendChild(indexLabel);

    // Add special indicators
    if (bagType === 'linked' && index === 0) {
      // Add "HEAD" indicator for linked list
      const headIndicator = document.createElement('div');
      headIndicator.textContent = 'HEAD';
      headIndicator.style.position = 'absolute';
      headIndicator.style.top = '-25px';
      headIndicator.style.left = '50%';
      headIndicator.style.transform = 'translateX(-50%)';
      headIndicator.style.fontSize = 'var(--font-size-xs)';
      headIndicator.style.fontWeight = 'var(--font-weight-bold)';
      headIndicator.style.color = 'var(--color-success)';
      headIndicator.style.backgroundColor = 'var(--theme-background)';
      headIndicator.style.padding = '2px 6px';
      headIndicator.style.borderRadius = 'var(--radius-sm)';
      headIndicator.style.border = '1px solid var(--color-success)';
      bar.appendChild(headIndicator);
    }

    return bar;
  }

  getBarColor(index, bagType, searchState) {
    // Highlight current search position
    if (searchState.currentSearchIndex === index) {
      return 'var(--color-warning)'; // Orange for current search position
    }

    // Highlight found element
    if (searchState.foundIndex === index) {
      return 'var(--color-success)'; // Green for found element
    }

    // Show searched positions as dimmed
    if (searchState.searchedIndices && searchState.searchedIndices.includes(index)) {
      return 'var(--color-error)'; // Red for already searched
    }

    // Special coloring for bag types
    if (bagType === 'linked' && index === 0) {
      return '#10b981'; // Green for head of linked list
    }

    // Access frequency based coloring (if available)
    if (searchState.accessFrequency && searchState.accessFrequency[index]) {
      const frequency = searchState.accessFrequency[index];
      if (frequency > 3) return '#f59e0b'; // Hot - frequently accessed
      if (frequency > 1) return '#3b82f6'; // Warm - occasionally accessed
    }

    return '#6b7280'; // Default gray for unaccessed elements
  }

  // Method to highlight search progression
  highlightSearch(searchIndex, bagType = 'array') {
    const bars = this.container.querySelectorAll('.search-bar');

    // Reset all bars
    bars.forEach((bar, index) => {
      bar.style.boxShadow = 'none';
      bar.style.transform = 'scale(1)';
      bar.style.zIndex = '1';
      bar.style.backgroundColor = this.getBarColor(index, bagType, { currentSearchIndex: -1 });
    });

    // Highlight current search position
    if (searchIndex >= 0 && searchIndex < bars.length) {
      const currentBar = bars[searchIndex];
      currentBar.style.backgroundColor = 'var(--color-warning)';
      currentBar.style.boxShadow = '0 0 15px var(--color-warning)';
      currentBar.style.transform = 'scale(1.05)';
      currentBar.style.zIndex = '10';
    }
  }

  // Method to show element found
  showFound(foundIndex, bagType = 'array') {
    const bars = this.container.querySelectorAll('.search-bar');
    if (foundIndex >= 0 && foundIndex < bars.length) {
      const foundBar = bars[foundIndex];
      foundBar.style.backgroundColor = 'var(--color-success)';
      foundBar.style.boxShadow = '0 0 20px var(--color-success)';
      foundBar.style.transform = 'scale(1.1)';
      foundBar.style.zIndex = '10';
    }
  }

  // Method to animate transpose (swap with predecessor)
  animateTranspose(index, bagType = 'array') {
    if (index <= 0) return;

    const bars = this.container.querySelectorAll('.search-bar');
    const currentBar = bars[index];
    const previousBar = bars[index - 1];

    if (!currentBar || !previousBar) return;

    // Add swap animation classes or direct animation
    currentBar.style.transform = 'translateX(-' + (currentBar.offsetWidth + 2) + 'px) scale(1.05)';
    previousBar.style.transform = 'translateX(' + (previousBar.offsetWidth + 2) + 'px) scale(1.05)';

    // Reset after animation
    setTimeout(() => {
      currentBar.style.transform = 'scale(1)';
      previousBar.style.transform = 'scale(1)';
    }, 500);
  }

  // Method to animate move-to-front
  animateMoveToFront(index, bagType = 'linked') {
    if (index <= 0) return;

    const bars = this.container.querySelectorAll('.search-bar');
    const movingBar = bars[index];

    if (!movingBar) return;

    // Calculate distance to move to front
    const distanceToMove = -index * (movingBar.offsetWidth + 2);

    // Animate to front
    movingBar.style.transform = `translateX(${distanceToMove}px) scale(1.1)`;
    movingBar.style.zIndex = '20';

    // Reset after animation and update structure
    setTimeout(() => {
      movingBar.style.transform = 'scale(1)';
      movingBar.style.zIndex = '1';
    }, 800);
  }
}